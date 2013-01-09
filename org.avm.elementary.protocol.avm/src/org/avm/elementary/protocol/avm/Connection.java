package org.avm.elementary.protocol.avm;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.avm.elementary.protocol.avm.parser.C_ACK;
import org.avm.elementary.protocol.avm.parser.C_CNX;
import org.avm.elementary.protocol.avm.parser.C_DCNX;
import org.avm.elementary.protocol.avm.parser.C_MSG;
import org.avm.elementary.protocol.avm.parser.Message;
import org.avm.elementary.protocol.avm.parser.PING;
import org.avm.elementary.protocol.avm.parser.PONG;
import org.avm.elementary.protocol.avm.parser.Parser;
import org.avm.elementary.protocol.avm.parser.S_ACK;
import org.avm.elementary.protocol.avm.parser.S_CNX;
import org.avm.elementary.protocol.avm.parser.S_DCNX;
import org.avm.elementary.protocol.avm.parser.S_MSG;
import org.avm.elementary.protocol.avm.state.ConnectionStateMachine;
import org.avm.elementary.protocol.avm.state.ConnectionStateMachineContext;

public abstract class Connection implements ConnectionStateMachine,
		Serializable {

	public static final long TIMEOUT = 20 * 1000;

	protected ConnectionStateMachineContext _state;

	protected SocketManager _manager;

	protected ConnectionListenerSupport _eventListeners;

	protected MessageListenerSupport _messageListeners;

	protected int _longitude;

	protected int _latitude;

	protected String _source;

	protected Date _date;

	protected long _period = 180;

	protected ConnectionListener _listener;

	protected volatile boolean _enabled;

	protected volatile long _current;

	protected Exception _exception;

	protected static final Timer _scheduler = new Timer();

	protected TimerTask _task;

	protected Object _lock = new Object();

	protected Connection(Socket socket, long period, ConnectionListener listener)
			throws Exception {

		_period = period;
		_listener = listener;

		_eventListeners = new ConnectionListenerSupport(this);
		_messageListeners = new MessageListenerSupport(this);
		addConnectionEventListener(_listener);

		_state = new ConnectionStateMachineContext(this);
		_state.setDebugFlag(false);
		_manager = new SocketManager(socket, _state);

	}

	public InetAddress getInetAddress() {
		return _manager.getInetAddress();
	}

	public int getState() {
		synchronized (_state) {
			try {
				return _state.getState().getId();
			} catch (statemap.StateUndefinedException e) {
				return -1;
			}
		}
	}

	protected void setException(Exception exception) {
		_exception = exception;
	}

	public void dispose() {
		removeConnectionEventListener(_listener);
		try {
			disconnect();
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	public void connect() throws Exception {
		_exception = null;
		_state.connect();
		if (_exception != null) {
			throw (_exception);
		}
	}

	public void disconnect() throws Exception {
		_exception = null;
		_exception = null;
		_state.disconnect();
		if (_exception != null) {
			throw (_exception);
		}
	}

	public void send(Message msg) throws Exception {
		_exception = null;
		synchronized (_lock) {
			_state.send(msg);
			if (_exception != null) {
				throw (_exception);
			}

			long now = System.currentTimeMillis();
			_lock.wait(TIMEOUT);
			boolean result = ((System.currentTimeMillis() - now) < TIMEOUT);
			if (result == false) {
				LogUtil.debug("[DSU] echec send : timeout");
				_state.disconnect();
				throw new TimeoutException();
			}
		}
	}

	public int getLongitude() {
		return _longitude;
	}

	public void setLongitude(int longitude) {
		_longitude = longitude;
	}

	public int getLatitude() {
		return _latitude;
	}

	public void setLatitude(int latitude) {
		_latitude = latitude;
	}

	public String getSource() {
		return _source;
	}

	public void setSource(String source) {
		_source = source;
	}

	public Date getDate() {
		return _date;
	}

	public void setDate(Date date) {
		_date = date;
	}

	protected void addConnectionEventListener(ConnectionListener listener) {
		_eventListeners.addConnectionEventListener(listener);
	}

	protected void removeConnectionEventListener(ConnectionListener listener) {
		_eventListeners.removeConnectionEventListener(listener);
	}

	public void addMessageEventListener(MessageListener listener) {
		_messageListeners.addConnectionEventListener(listener);
	}

	public void removeMessageEventListener(MessageListener listener) {
		_messageListeners.removeConnectionEventListener(listener);
	}

	protected static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	// gestion timer --------------------------------------------------------//

	protected void resetTimeout() {
		stopTimer();
		startTimer();
		LogUtil.debug("[DSU] reset timer");
	}

	protected abstract void timeoutCallback();

	protected void alarm() {
		if (_task == null) {
			_task = new TimerTask() {
				public void run() {
					LogUtil.debug("[DSU] echec : timeout !");
					_state.disconnect();
				}
			};
			_scheduler.schedule(_task, TIMEOUT);
		}
	}

	protected void cancel() {
		if (_task != null) {
			_task.cancel();
			_task = null;
		}
	}

	// statemachine callback -------------------------------------------------//

	public synchronized void startTimer() {
		_current = System.currentTimeMillis();
		_enabled = true;
	}

	public synchronized void stopTimer() {
		_enabled = false;
	}

	public void defaultCallback() {
		LogUtil.debug("[DSU] Connection.defaultCallback()");
	}

	protected void process(Message msg) {

		switch (msg.getType()) {
		case C_CNX.MESSAGE_TYPE:
			_state.connected(msg);
			break;
		case S_CNX.MESSAGE_TYPE:
			_state.connected(msg);
			break;
		case C_DCNX.MESSAGE_TYPE:
			_state.disconnect();
			break;
		case S_DCNX.MESSAGE_TYPE:
			_state.disconnect();
			break;
		case PING.MESSAGE_TYPE:
			_state.ping(msg);
			break;
		case PONG.MESSAGE_TYPE:
			_state.pong(msg);
			break;
		case C_MSG.MESSAGE_TYPE:
			_state.receive(msg);
			break;
		case S_ACK.MESSAGE_TYPE:
			_state.acknolege(msg);
			break;
		case S_MSG.MESSAGE_TYPE:
			_state.receive(msg);
			break;
		case C_ACK.MESSAGE_TYPE:
			_state.acknolege(msg);
			break;
		default:
			break;
		}

	}

	// debug ------- ---------------------------------------------------------//

	public static boolean isDebugEnabled() {
		return LogUtil.isDebugEnabled();
	}

	public void setDebugEnabled(boolean debugEnabled) {
		LogUtil.setDebugEnabled(debugEnabled);
	}

	// reader ----------------------------------------------------------------//
	class SocketManager implements Runnable {

		private Socket _socket;

		private InputStream _in;

		private OutputStream _out;

		private Thread _target;

		private volatile boolean _running;

		private Parser _parser;

		private ConnectionStateMachineContext _state;

		public SocketManager(Socket socket, ConnectionStateMachineContext state)
				throws Exception {
			_socket = socket;
			_socket.setSoTimeout(3000);
			_state = state;
			_parser = new Parser();

		}

		public InetAddress getInetAddress() {
			InetAddress result = null;
			if (_socket != null) {
				result = _socket.getInetAddress();
			}
			return result;
		}

		public synchronized boolean isRunning() {
			return _running;
		}

		public synchronized void start() throws IOException {
			if (!_running) {
				_running = true;
				try {
					_in = new BufferedInputStream(_socket.getInputStream());
					_out = _socket.getOutputStream();
				} catch (IOException e) {
					throw e;
				}
				_target = new Thread(this);
				_target.setDaemon(true);
				_target.start();
			}
		}

		public synchronized void stop() {
			if (_running) {
				_running = false;
			}
		}

		public void send(Message msg) throws Exception {
			if (_running) {
				try {
					LogUtil.debug("[DSU] send : " + msg.toString());
					_parser.put(msg, _out);
					_out.flush();
				} catch (Exception e) {
					stop();
					throw e;
				}
			}
		}

		protected Message receive() throws Exception {
			Message msg = null;
			try {
				msg = (Message) _parser.get(_in);
			} catch (InterruptedIOException e) {
				// LogUtil.debug("[DSU] socket timeout");
			} catch (EOFException e) {
				LogUtil.debug("[DSU] eof exception");
				throw e;
			} catch (SocketException e) {
				LogUtil.error("[DSU] socket exception", e);
				throw e;
			} catch (IOException e) {
				LogUtil.debug("[DSU] io exception");
				throw e;
			}
			return msg;
		}

		public void run() {

			try {
				LogUtil.debug("[DSU] reader started uid = "
						+ ObjectStreamClass.lookup(Connection.class)
								.getSerialVersionUID());

				while (isRunning()) {
					Message msg = receive();
					if (msg != null) {
						LogUtil.debug("[DSU] receive : " + msg.toString());
						process(msg);
					}

					if (_enabled
							&& ((System.currentTimeMillis() - _current) > (_period * 1000))) {
						timeoutCallback();
					}

				}
			} catch (Throwable e) {
				LogUtil.error("[DSU] echec receive : " + e.getMessage(), e);
				stop();
			} finally {
				_state.disconnect();
				try {
					_in.close();
					_out.close();
					_socket.close();
				} catch (IOException e) {
					LogUtil.error("[DSU] echec reader stoped", e);
				}
				LogUtil.debug("[DSU] reader stoped");
			}

		}

		private void sleep(long time) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
			}
		}
	}

}
