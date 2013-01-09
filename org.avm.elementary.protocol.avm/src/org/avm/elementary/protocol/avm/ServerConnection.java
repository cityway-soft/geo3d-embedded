package org.avm.elementary.protocol.avm;

import java.net.Socket;
import java.util.Date;

import org.avm.elementary.protocol.avm.parser.C_ACK;
import org.avm.elementary.protocol.avm.parser.C_CNX;
import org.avm.elementary.protocol.avm.parser.C_MSG;
import org.avm.elementary.protocol.avm.parser.Message;
import org.avm.elementary.protocol.avm.parser.PING;
import org.avm.elementary.protocol.avm.parser.PONG;
import org.avm.elementary.protocol.avm.parser.S_ACK;
import org.avm.elementary.protocol.avm.parser.S_CNX;
import org.avm.elementary.protocol.avm.parser.S_DCNX;

public class ServerConnection extends Connection {

	public ServerConnection(Socket socket, long period,
			ConnectionListener listener) throws Exception {
		super(socket, period, listener);
	}

	protected void timeoutCallback() {
		if (_state != null) {
			_state.disconnect();
		}
		resetTimeout();
	}

	// statemachine callback -------------------------------------------------//

	public boolean connectingCallback() {
		boolean result = false;
		LogUtil.debug("[DSU] connection connecting");
		try {
			_manager.start();
			result = true;
		} catch (Exception e) {
			setException(e);
			LogUtil.error("[DSU] echec connection connecting", e);
		}
		return result;
	}

	public boolean connectedCallback(Message msg) {
		boolean result = false;
		try {
			LogUtil.debug("[DSU] connection connected");
			C_CNX message = (C_CNX) msg;
			_source = message.getSource();
			_longitude = message.getLongitude();
			_latitude = message.getLatitude();
			_date = new Date();
			_manager.send(new S_CNX());
			_eventListeners.fireConnectionEvent(ConnectionEvent.CONNECTED);
			result = true;
		} catch (Exception e) {
			LogUtil.error("[DSU] echec connection connected", e);
		}
		return result;
	}

	public void disconnectingCallback() {
		LogUtil.debug("[DSU] connection disconnecting");
		try {
			_manager.send(new S_DCNX());
		} catch (Exception e) {
			LogUtil.error("[DSU] echec connection disconnecting", e);
		} finally {
			_manager.stop();
			_eventListeners.fireConnectionEvent(ConnectionEvent.DISCONNECTED);
		}
	}

	public boolean pingCallback(Message msg) {
		boolean result = false;
		try {
			LogUtil.debug("[DSU] connection acknolege");
			resetTimeout();
			PING message = (PING) msg;
			_longitude = message.getLongitude();
			_latitude = message.getLatitude();
			_date = new Date();
			resetTimeout();
			_manager.send(new PONG());
			_eventListeners.fireConnectionEvent(ConnectionEvent.KEEPALIVE);
			result = true;
		} catch (Exception e) {
			LogUtil.error("[DSU] echec connection acknolege", e);
		}
		return result;
	}

	public boolean pongCallback(Message msg) {
		LogUtil.debug("[DSU] connection pong");
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

	public boolean receiveCallback(Message msg) {
		boolean result = false;
		try {
			LogUtil.debug("[DSU] connection receive");
			resetTimeout();

			C_MSG message = (C_MSG) msg;
			_longitude = message.getLongitude();
			_latitude = message.getLatitude();
			_date = new Date();
			_messageListeners.fireMessageEvent(msg);

			S_ACK acknolege = new S_ACK();
			_manager.send(acknolege);

			result = true;
		} catch (Exception e) {
			LogUtil.error("[DSU] echec connection receive", e);
		}
		return result;
	}

	public boolean acknolegeCallback(Message msg) {
		boolean result = false;
		try {
			LogUtil.debug("[DSU] connection acknolege");
			resetTimeout();
			synchronized (_lock ) {
				_lock.notifyAll();
			}
			C_ACK message = (C_ACK) msg;
			_longitude = message.getLongitude();
			_latitude = message.getLatitude();
			_date = new Date();
			_messageListeners.fireMessageEvent(msg);
			result = true;
		} catch (Exception e) {
			LogUtil.error("[DSU] echec connection acknolege", e);
		}
		return result;
	}

}
