package org.avm.elementary.protocol.avm;

import java.net.Socket;

import org.avm.elementary.protocol.avm.parser.C_ACK;
import org.avm.elementary.protocol.avm.parser.C_CNX;
import org.avm.elementary.protocol.avm.parser.C_DCNX;
import org.avm.elementary.protocol.avm.parser.Message;
import org.avm.elementary.protocol.avm.parser.PING;

public class ClientConnection extends Connection {

	public ClientConnection(Socket socket, long period,
			ConnectionListener listener) throws Exception {
		super(socket, period, listener);
	}

	protected void timeoutCallback() {
		if (_state != null) {
			_state.ping(new PING(_longitude, _latitude));
		}
		resetTimeout();
	}

	// statemachine callback -------------------------------------------------//

	public boolean connectingCallback() {
		boolean result = false;
		LogUtil.debug("[DSU] connection connecting");
		try {
			_manager.start();
			sleep(1000); // attente ouverture socket
			C_CNX msg = new C_CNX(_longitude, _latitude, _source);
			_manager.send(msg);
			alarm();
			result = true;
		} catch (Exception e) {
			setException(e);
			LogUtil.error("[DSU] echec connection connecting", e);
		}
		return result;
	}

	public boolean connectedCallback(Message msg) {
		LogUtil.debug("[DSU] connection connected");
		cancel();
		_eventListeners.fireConnectionEvent(ConnectionEvent.CONNECTED);
		return true;
	}

	public void disconnectingCallback() {
		LogUtil.debug("[DSU] connection disconnecting");
		try {
			_manager.send(new C_DCNX());
		} catch (Exception e) {
			LogUtil.error("[DSU] echec connection disconnecting", e);
		} finally {
			_manager.stop();
			_eventListeners.fireConnectionEvent(ConnectionEvent.DISCONNECTED);
		}
	}

	public boolean pingCallback(Message msg) {
		boolean result = false;
		LogUtil.debug("[DSU] connection ping");
		try {
			_manager.send(msg);
			alarm();
			result = true;
		} catch (Exception e) {
			setException(e);
			LogUtil.error("[DSU] echec connection ping", e);
		}
		return result;
	}

	public boolean pongCallback(Message msg) {
		LogUtil.debug("[DSU] connection pong");
		cancel();
		resetTimeout();
		_eventListeners.fireConnectionEvent(ConnectionEvent.KEEPALIVE);
		return true;
	}

	public boolean sendCallback(Message msg) {
		boolean result = false;
		LogUtil.debug("[DSU] connection send");
		try {
			_manager.send(msg);
			result = true;
		} catch (Exception e) {
			setException(e);
			LogUtil.error("[DSU] echec connection send", e);
		}
		return result;
	}

	public boolean acknolegeCallback(Message msg) {
		LogUtil.debug("[DSU] connection acknolege");
		resetTimeout();
		synchronized (_lock ) {
			_lock.notifyAll();
		}
		_messageListeners.fireMessageEvent(msg);
		return true;
	}

	public boolean receiveCallback(Message msg) {
		boolean result = false;
		LogUtil.debug("[DSU] connection receive");
		resetTimeout();
		try {
			C_ACK acknolege = new C_ACK(_longitude, _latitude);
			_manager.send(acknolege);
			_messageListeners.fireMessageEvent(msg);
			result = true;
		} catch (Exception e) {
			LogUtil.error("[DSU] echec connection receive", e);
		}
		return result;
	}

}
