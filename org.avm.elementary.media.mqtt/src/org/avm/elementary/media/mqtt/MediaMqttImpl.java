package org.avm.elementary.media.mqtt;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
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
import org.avm.elementary.media.mqtt.bundle.Activator;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.osgi.util.position.Position;

public class MediaMqttImpl implements MediaMqtt, ConfigurableService,
		ManageableService, JDBInjector, AlarmProvider, ProducerService,
		MqttCallback {

	private static final String MQTT_DISCONNECT = "DISCONNECT";

	private static final String MQTT_ON_LINE = "ON LINE";

	private static final String MQTT_CONNECTION_LOST = "CONNECTION LOST";

	private final String JDB_TAG = "COMM";

	private Logger _log;

	private MediaMqttConfig _config;

	private MediaListener _messenger;

	private static final int DEFAULT_TIMEOUT = 1000;

	private int _timeout = DEFAULT_TIMEOUT;

	private int _failureCounter = 0;

	private Scheduler _scheduler = new Scheduler();

	private boolean _started = false;

	private Alarm alarm;

	private JDB jdb;

	private ProducerManager _producer;

	private Date _dateConnection;

	private MqttClient mqttClient;

	private String topicMO;

	private String topicMT;

	private int lon;

	private int lat;

	private int speed;

	private int track;

	private Date date;

	public MediaMqttImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		alarm = new Alarm(new Integer(20));
	}

	public void configure(Config config) {
		_config = (MediaMqttConfig) config;
		topicMO = "/terminal/mo/" + getMediaId();
		topicMT = "/terminal/mt";
	}

	public void setMessenger(MediaListener messenger) {
		_messenger = messenger;
		if (_messenger != null) {
			_messenger.setMedia(this);
		}
	}

	public void start() {
		_started = true;
		_scheduler.execute(INITIALIZE_CONNECTION);

	}

	public void stop() {

		_scheduler.execute(DISPOSE_CONNECTION);
		_started = false;
	}

	public String getMediaId() {
		return _config.getMediaId();
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		if (header != null) {
			saveContextIntoHeader(header);
		}

		Integer lon = (Integer) header.get("lon");
		Integer lat = (Integer) header.get("lat");
		Integer trk = (Integer) header.get("trk");
		Integer spd = (Integer) header.get("spd");
		Date date = (Date) header.get("date");

		// BaseData base = new BaseData();
		// LocalisedData location = new LocalisedData();
		// location.setLat((double)lat.intValue());
		// location.setLon((double)lon.intValue());
		// location.setSpeed((double)spd.intValue());
		// location.setTrack((double)trk.intValue());
		// location.setDate(date);
		// M2MMessage m = M2MMessageHelper.createM2MMessage(base, location,
		// data);
		// String msg = M2MMessageHelper.toJson(m);
		//
		// byte[] dataToSend=msg.getBytes();

		byte[] dataToSend = data;

		int qos = 2;
		MqttMessage message = new MqttMessage(dataToSend);
		message.setQos(qos);
		mqttClient.publish(topicMO, message);
		_log.debug("Message published");
		// if (_started) {
		// if (_connection.getState() == 2) {
		// _send(header, data);
		// notifyConnected(true);
		// } else {
		// //
		// _log.warn("Failed to send msg with header ("+header.hashCode()+") "
		// // + header);
		// notifyConnected(false);
		// throw new Exception("Failure : media not connected");
		// }
		// } else {
		// throw new Exception("Failure : media not started");
		// }
	}

	public void notify(Object o) {
		if (o instanceof Position) {
			Position position = (Position) o;
			lon = (int) (position.getLongitude().getValue() * 180 / Math.PI * 360000);
			lat = (int) (position.getLatitude().getValue() * 180 / Math.PI * 360000);
			speed = (int) (position.getSpeed().getValue() * 3.6);
			track = (int) (position.getTrack().getValue() * 180 / Math.PI);

			// if (_connection != null) {
			// _connection.setLongitude(lon);
			// _connection.setLatitude(lat);
			// _connection.setSpeed(speed);
			// _connection.setTrack(track);
			// }
		}
	}

	private void saveContextIntoHeader(Dictionary header) {
		Integer lon = (Integer) header.get("lon");
		if (lon == null) {
			lon = new Integer(this.lon);
			header.put("lon", lon);
		}

		Integer lat = (Integer) header.get("lat");
		if (lat == null) {
			lat = new Integer(this.lat);
			header.put("lat", lat);
		}

		Integer trk = (Integer) header.get("trk");
		if (trk == null) {
			trk = new Integer(this.track);
			header.put("trk", trk);
		}

		Integer spd = (Integer) header.get("spd");
		if (spd == null) {
			spd = new Integer(this.speed);
			header.put("spd", spd);
		}

		Date date = (Date) header.get("date");
		if (date == null) {
			date = this.date;
			if (date == null) {
				date = new Date();
			}
			header.put("date", date);
		}
	}

	// public String toString() {// _state.connected();
	// String result = getStateName((_connection != null) ? _connection
	// .getState() : -1);
	// return result;
	// }

	// private void _send(Dictionary header, byte[] data) throws Exception {
	// _log.debug("Media send");
	// try {
	// Integer lon = (Integer) header.get("lon");
	// Integer lat = (Integer) header.get("lat");
	// Integer trk = (Integer) header.get("trk");
	// Integer spd = (Integer) header.get("spd");
	// Date date = (Date) header.get("date");
	//
	// C_MSG msg = new C_MSG(lon.intValue(), lat.intValue(),
	// trk.intValue(), spd.intValue(), data);
	// msg.setHorodate(new Horodate(date.getTime()));
	//
	// if (_log.isDebugEnabled()) {
	// _log.debug("Send message with header:" + header);
	// }
	//
	// _connection.send(msg);
	// } catch (Exception e) {
	// _log.warn("Failed to send msg with header (" + header.hashCode()
	// + ") " + header);
	//
	// _log.error(e);
	// throw new Exception(e.getMessage());
	// }
	// }

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

			_log.info("Opening Media MQTT on host: " + address + " port: "
					+ port + " period: " + period + " timeout:" + _timeout
					+ " failure:" + _failureCounter);

			// Socket socket = new Socket(host, port);
			// socket.setSoTimeout(10000);
			//
			try {
				String broker = "tcp://" + host + ":" + port;
				String clientId = getMediaId();
				MemoryPersistence persistence = new MemoryPersistence();
				mqttClient = new MqttClient(broker, clientId, persistence);

				MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setCleanSession(true);
				connOpts.setWill(topicMO, MQTT_CONNECTION_LOST.getBytes(), 2,
						true);
				if (_log.isDebugEnabled()) {
					_log.debug("Connecting to broker: " + broker);
				}
				mqttClient.connect(connOpts);
				send(new Hashtable(), MQTT_ON_LINE.getBytes());
				mqttClient.setCallback(MediaMqttImpl.this);
				System.out.println("Connected");
				// -- subscribe to topic for all terminals
				mqttClient.subscribe(topicMT);
				// -- subscribe to topic for this terminal only
				mqttClient.subscribe(topicMT + "/" + getMediaId());

				notifyConnected(true);
			} catch (MqttException me) {
				_log.warn("reason " + me.getReasonCode());
				_log.warn("msg " + me.getMessage());
				_log.warn("loc " + me.getLocalizedMessage());
				_log.warn("cause " + me.getCause());
				_log.warn("excep " + me);
				if (_log.isDebugEnabled()) {
					me.printStackTrace();
				}
				throw me;
			}

			// socket.setTcpNoDelay(false);

			// _connection = new ClientConnection(socket, period,
			// MediaMqttImpl.this);
			//
			// _connection.addMessageEventListener(MediaMqttImpl.this);
			// _connection.setDebugEnabled(_log.isDebugEnabled());
			// _connection.setTerminalId(_config.getMediaId());
			// _connection.connect();

			_log.debug("Media Mqtt opened");
		}

	};

	private Runnable DISPOSE_CONNECTION = new Runnable() {

		public void run() {
			dispose();
		}

		private void dispose() {
			// if (_connection != null) {
			// _connection.dispose();
			// _connection = null;
			// }
			try {
				send(new Hashtable(), MQTT_DISCONNECT.getBytes());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					mqttClient.disconnect();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			_log.debug("[Media Mqtt disposed");
		}
	};

	// connection callback ---------------------------------------------------//

	// public void keepalive(ConnectionEvent event) {
	//
	// }
	//
	// public void connected(ConnectionEvent event) {
	// jdb.journalize(JDB_TAG, "CONNECTED");
	// _log.info("Media CTW connected");
	// }
	//
	// public void disconnected(ConnectionEvent event) {
	// jdb.journalize(JDB_TAG, "DISCONNECTED");
	// _log.info("Media CTW disconnected");
	// if (_started) {
	// _scheduler.execute(INITIALIZE_CONNECTION);
	// }
	//
	// }
	//
	// public void receive(MessageEvent event) {
	// _log.debug("Media CTW receive message");
	// S_MSG message = (S_MSG) event.getMessage();
	// Dictionary header = new Properties();
	// byte[] data = message.getValue();
	// _messenger.receive(header, data);
	// }
	//
	// public void acknowledge(MessageEvent event) {
	// _log.debug("Media CTW acknoledge");
	// }

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

	public void connectionLost(Throwable arg0) {
		_log.debug("Media MQTT connectionLost : " + arg0);
		notifyConnected(false);

	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		_log.debug("Media MQTT acknoledge : " + arg0);

	}

	public void messageArrived(String arg0, MqttMessage message)
			throws Exception {
		byte[] data = message.getPayload();
		String msg = new String(data);
		if (msg.contains(MQTT_ON_LINE)) {
			_log.info("** SERVER ON LINE **");
			send(new Hashtable(), MQTT_ON_LINE.getBytes());
			notifyConnected(true);
		} else if (msg.contains(MQTT_CONNECTION_LOST)) {
			_log.info("** SERVER ON LINE **");
			notifyConnected(false);
		} else {
			Dictionary header = new Properties();
			_messenger.receive(header, data);
		}
	}
}