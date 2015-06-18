package org.avm.elementary.media.ctw;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.media.ctw.bundle.Activator;
import org.avm.elementary.protocol.ctw.ClientConnection;
import org.avm.elementary.protocol.ctw.ConnectionEvent;
import org.avm.elementary.protocol.ctw.ConnectionListener;
import org.avm.elementary.protocol.ctw.MessageEvent;
import org.avm.elementary.protocol.ctw.MessageListener;
import org.avm.elementary.protocol.ctw.parser.C_MSG;
import org.avm.elementary.protocol.ctw.parser.Horodate;
import org.avm.elementary.protocol.ctw.parser.S_MSG;
import org.osgi.util.position.Position;

public class MediaCTWImpl implements MediaCTW, ConfigurableService,
		ManageableService, ConnectionListener, MessageListener, JDBInjector,
		AlarmProvider, ProducerService {

	private final String JDB_TAG = "COMM";

	private Logger _log;

	private MediaCTWConfig _config;

	private MediaListener _messenger;

	private ClientConnection _connection;

	private static final int DEFAULT_TIMEOUT = 1000;

	private int _timeout = DEFAULT_TIMEOUT;

	private int _failureCounter = 0;

	private Scheduler _scheduler = new Scheduler();

	private boolean _started = false;

	private Alarm alarm;

	private JDB jdb;

	private ProducerManager _producer;

	private Date _dateConnection;

	public MediaCTWImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		alarm = new Alarm(new Integer(20));
	}

	public void configure(Config config) {
		_config = (MediaCTWConfig) config;
	}

	public void setMessenger(MediaListener messenger) {
		_messenger = messenger;
		if (_messenger != null){
			_messenger.setMedia(this);
		}
	}

	public void start() {
		_started = true;
		_scheduler.execute(INITIALIZE_CONNECTION);
	}

	public void stop() {
		_started = false;
		DISPOSE_CONNECTION.run();
	}

	public String getMediaId() {
		return _config.getMediaId();
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		saveContextIntoHeader(header);
		if (_started) {
			if (_connection.getState() == 2) {
				_send(header, data);
				notifyConnected(true);
			} else {
				// _log.warn("Failed to send msg with header ("+header.hashCode()+") "
				// + header);
				notifyConnected(false);
				throw new Exception("Failure : media not connected");
			}
		} else {
			throw new Exception("Failure : media not started");
		}
	}

	public void notify(Object o) {
		if (o instanceof Position) {
			Position position = (Position) o;
			int lon = (int) (position.getLongitude().getValue() * 180 / Math.PI * 360000);
			int lat = (int) (position.getLatitude().getValue() * 180 / Math.PI * 360000);
			int speed = (int) (position.getSpeed().getValue() * 3.6);
			int track = (int) (position.getTrack().getValue() * 180 / Math.PI);

			if (_connection != null) {
				_connection.setLongitude(lon);
				_connection.setLatitude(lat);
				_connection.setSpeed(speed);
				_connection.setTrack(track);
			}
		}
	}

	private void saveContextIntoHeader(Dictionary header) {
		Integer lon = (Integer) header.get("lon");
		if (lon == null) {
			lon = new Integer(_connection.getLongitude());
			header.put("lon", lon);
		}

		Integer lat = (Integer) header.get("lat");
		if (lat == null) {
			lat = new Integer(_connection.getLatitude());
			header.put("lat", lat);
		}

		Integer trk = (Integer) header.get("trk");
		if (trk == null) {
			trk = new Integer(_connection.getTrack());
			header.put("trk", trk);
		}

		Integer spd = (Integer) header.get("spd");
		if (spd == null) {
			spd = new Integer(_connection.getSpeed());
			header.put("spd", spd);
		}

		Date date = (Date) header.get("date");
		if (date == null) {
			date = _connection.getDate();
			if (date == null) {
				date = new Date();
			}
			header.put("date", date);
		}
	}

	public String toString() {// _state.connected();
		String result = getStateName((_connection != null) ? _connection
				.getState() : -1);
		return result;
	}

	private void _send(Dictionary header, byte[] data) throws Exception {
		_log.debug("Media send");
		try {
			Integer lon = (Integer) header.get("lon");
			Integer lat = (Integer) header.get("lat");
			Integer trk = (Integer) header.get("trk");
			Integer spd = (Integer) header.get("spd");
			Date date = (Date) header.get("date");

			C_MSG msg = new C_MSG(lon.intValue(), lat.intValue(),
					trk.intValue(), spd.intValue(), data);
			msg.setHorodate(new Horodate(date.getTime()));

			if (_log.isDebugEnabled()) {
				_log.debug("Send message with header:" + header);
			}

			_connection.send(msg);
		} catch (Exception e) {
			_log.warn("Failed to send msg with header (" + header.hashCode()
					+ ") " + header);

			_log.error(e);
			throw new Exception(e.getMessage());
		}
	}

	private void notifyConnected(boolean b) {
		boolean current = (!b);
		boolean previous = alarm.isStatus();
		if (previous != current) {
			alarm.setStatus(current);
			_log.info("Alarm :" + alarm);
			_log.info("State changed => Publish");
			_producer.publish(alarm);
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
				_timeout = DEFAULT_TIMEOUT;
				_failureCounter = 0;
				_dateConnection = new Date();
				notifyConnected(true);
			} catch (Exception e) {
				if (_started) {
					_scheduler.schedule(INITIALIZE_CONNECTION, _timeout);
					_failureCounter++;
					if (_failureCounter % 5 == 0 && _timeout < (3 * 60000)) {
						notifyConnected(false);
						_timeout = (_failureCounter / 5) * 5000;
					}
				}
			}
		}

		private void initilize() throws Exception {

			String host = _config.getAddress();
			InetAddress address = InetAddress.getByName(host);
			int port = _config.getPort().intValue();
			int period = _config.getPeriod().intValue();

			_log.info("Opening Media CTW on host: " + address + " port: "
					+ port + " period: " + period + " timeout:" + _timeout
					+ " failure:" + _failureCounter);

			Socket socket = new Socket(host, port);
			socket.setSoTimeout(10000);
			
			// socket.setTcpNoDelay(false);

			_connection = new ClientConnection(socket, period,
					MediaCTWImpl.this);

			_connection.addMessageEventListener(MediaCTWImpl.this);
			_connection.setDebugEnabled(_log.isDebugEnabled());
			_connection.setTerminalId(_config.getMediaId());
			_connection.connect();

			_log.debug("Media CTW opened");
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
			_log.debug("[Media CTW diposed");
		}
	};

	// connection callback ---------------------------------------------------//

	public void keepalive(ConnectionEvent event) {

	}

	public void connected(ConnectionEvent event) {
		jdb.journalize(JDB_TAG, "CONNECTED");
		_log.info("Media CTW connected");
	}

	public void disconnected(ConnectionEvent event) {
		jdb.journalize(JDB_TAG, "DISCONNECTED");
		_log.info("Media CTW disconnected");
		if (_started) {
			_scheduler.execute(INITIALIZE_CONNECTION);
		}

	}

	public void receive(MessageEvent event) {
		_log.debug("Media CTW receive message");
		S_MSG message = (S_MSG) event.getMessage();
		Dictionary header = new Properties();
		byte[] data = message.getValue();
		_messenger.receive(header, data);
	}

	public void acknowledge(MessageEvent event) {
		_log.debug("Media CTW acknoledge");
	}

	public int getFailureCount() {
		return _failureCounter;
	}

	public void setJdb(JDB jdb) {
		this.jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		this.jdb = jdb;
	}

	public List getAlarm() {
		List list = null;
		if (alarm.isStatus()) {
			list = new ArrayList();
			list.add(alarm);
		}
		return list;
	}

	public String getProducerPID() {
		return Activator.PID;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public Date getConnectionDate() {
		return _dateConnection;
	}
}