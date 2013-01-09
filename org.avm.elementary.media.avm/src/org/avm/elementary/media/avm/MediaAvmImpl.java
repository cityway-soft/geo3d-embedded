package org.avm.elementary.media.avm;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.protocol.avm.ClientConnection;
import org.avm.elementary.protocol.avm.ConnectionEvent;
import org.avm.elementary.protocol.avm.ConnectionListener;
import org.avm.elementary.protocol.avm.MessageEvent;
import org.avm.elementary.protocol.avm.MessageListener;
import org.avm.elementary.protocol.avm.parser.C_MSG;
import org.avm.elementary.protocol.avm.parser.Message;
import org.avm.elementary.protocol.avm.parser.S_MSG;
import org.osgi.util.position.Position;

public class MediaAvmImpl implements MediaAvm, ConfigurableService,
		ManageableService, ConnectionListener, MessageListener {

	private Logger _log;

	private MediaAvmConfig _config;

	private MediaListener _messenger;

	private ClientConnection _connection;

	private Scheduler _scheduler = new Scheduler();

	private boolean _started = false;

	public MediaAvmImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
	}

	public void configure(Config config) {
		_config = (MediaAvmConfig) config;
	}

	public void setMessenger(MediaListener messenger) {
		_messenger = messenger;
	}

	public void start() {
		_started = true;
		_scheduler.execute(INITIALIZE_CONNECTION);
	}

	public void stop() {
		_started = false;
		_scheduler.execute(DISPOSE_CONNECTION);
	}

	public String getMediaId() {
		return _config.getMediaId();
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		if (_started) {
			if (_connection.getState() == 2) {
				_send(header, data);
			} else {
				throw new Exception("[DSU] echec send : media not connected");
			}
		} else {
			throw new Exception("[DSU] echec send : media not started");
		}
	}

	public void notify(Object o) {
		if (o instanceof Position) {
			Position position = (Position) o;
			int lon = (int) (position.getLongitude().getValue() * 180 / Math.PI * 360000);
			int lat = (int) (position.getLatitude().getValue() * 180 / Math.PI * 360000);
			if (_connection != null) {
				_connection.setLongitude(lon);
				_connection.setLatitude(lat);
			}
		}
	}

	public String toString() {// _state.connected();
		String result = getStateName((_connection != null) ? _connection
				.getState() : -1);
		return result;
	}

	private void _send(Dictionary header, byte[] data) throws Exception {
		_log.debug("[DSU] media send");
		try {
			String dest = (String) header.get("MESSAGE_DEST_ID");
			int lon = _connection.getLongitude();
			int lat = _connection.getLatitude();
			Message msg = new C_MSG(lon, lat, dest, data);
			_connection.send(msg);
		} catch (Exception e) {
			_log.error(e);
			throw new Exception(e.getMessage());
		}
	}

	private String getStateName(int value) {
		String result = null;
		switch (value) {
		case -1:
			result = "DEFAULT";
			break;
		case 0:
			result = "DISCONNECTED";// _state.connected();
			break;
		case 1:
			result = "CONNECTING";
			break;
		case 2:
			result = "CONNECTED";
			break;
		}
		return result;
	}

	private Runnable INITIALIZE_CONNECTION = new Runnable() {
		public void run() {
			try {
				initilize();
			} catch (Exception e) {
				if (_started) {
					_scheduler.schedule(INITIALIZE_CONNECTION, 1000);
				}
			}
		}

		private void initilize() throws Exception {

			String host = _config.getAddress();
			InetAddress address = InetAddress.getByName(host);
			int port = _config.getPort().intValue();
			int period = _config.getPeriod().intValue();

			_log.debug("[DSU] initialisation du service host: " + address
					+ " port: " + port + " period: " + period);

			Socket socket = new Socket(host, port);
			// socket.setTcpNoDelay(false);

			_connection = new ClientConnection(socket, period,
					MediaAvmImpl.this);

			_connection.addMessageEventListener(MediaAvmImpl.this);
			_connection.setDebugEnabled(_log.isDebugEnabled());
			_connection.setSource(_config.getMediaId());
			_connection.connect();

			_log.debug("[DSU] media initilized");
		}

	};

	private Runnable DISPOSE_CONNECTION = new Runnable() {

		public void run() {
			dispose();
		}

		private void dispose() {
			if (_connection != null) {
				_connection.dispose();
				_connection = null;
			}
			_log.debug("[DSU] media diposed");
		}
	};

	// connection callback ---------------------------------------------------//

	public void keepalive(ConnectionEvent event) {

	}

	public void connected(ConnectionEvent event) {
		_log.debug("[DSU] connection connected");
	}

	public void disconnected(ConnectionEvent event) {
		_log.debug("[DSU] connection disconnected");
		if (_started) {
			_scheduler.execute(INITIALIZE_CONNECTION);
		}

	}

	public void receive(MessageEvent event) {
		_log.debug("[DSU] connection receive");
		S_MSG message = (S_MSG) event.getMessage();
		Dictionary header = new Properties();
		byte[] data = message.getValue();
		_messenger.receive(header, data);
	}

	public void acknoledge(MessageEvent event) {
		_log.debug("[DSU] connection acknoledge");
	}
}