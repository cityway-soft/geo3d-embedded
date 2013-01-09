package org.avm.elementary.media.jms;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.MediaListener;
import org.jboss.mq.SpyConnectionFactory;
import org.jboss.mq.il.ServerILFactory;
import org.jboss.mq.il.uil2.UILServerILFactory;

/**
 * @author root
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MediaJMSImpl implements MediaJMS, ConfigurableService,
		ManageableService, MessageListener, ExceptionListener, Runnable,
		Mediator {

	private StateManager _manager;

	private Thread _target;

	private boolean _shouldRun;

	private TopicConnection _topicConnection = null;

	private TopicSession _topicSession = null;

	private Topic _topic = null;

	private TopicSubscriber _topicSubscriber = null;

	private TopicPublisher _topicPublisher = null;

	private long _openingCallbackCounter;

	private Logger _log;

	private MediaJMSConfig _config;

	private MediaListener _messenger;

	public MediaJMSImpl() {
		_log = Logger.getInstance(this.getClass());
		_manager = new StateManager(this);
	}

	public void configure(Config config) {
		_config = (MediaJMSConfig) config;
	}

	private ConnectionFactory createConnectionFactory() {
		Properties config = new Properties();
		config.put(ServerILFactory.SERVER_IL_FACTORY_KEY, _config
				.getServerILFactory());
		config.put(ServerILFactory.CLIENT_IL_SERVICE_KEY, _config
				.getClientILService());
		config.put(ServerILFactory.PING_PERIOD_KEY, _config.getPingPeriod()
				.toString());
		config.put(UILServerILFactory.UIL_ADDRESS_KEY, _config.getUilAddress());
		config.put(UILServerILFactory.UIL_PORT_KEY, _config.getUilPort()
				.toString());
		config.put(UILServerILFactory.UIL_TCPNODELAY_KEY, _config
				.getUilTCPNoDelay().toString());
		config.put(UILServerILFactory.UIL_CHUNKSIZE_KEY, _config
				.getUilChrunkSize().toString());
		config.put(UILServerILFactory.UIL_BUFFERSIZE_KEY, _config
				.getUilBufferSize().toString());
		ConnectionFactory factory = new SpyConnectionFactory(config);
		return factory;
	}

	private void initialize() throws Exception {

		TopicConnectionFactory factory = (TopicConnectionFactory) createConnectionFactory();
		_topicConnection = factory.createTopicConnection();
		_topicConnection.setClientID(_config.getMediaId());

		_topicSession = _topicConnection.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);

		_topic = _topicSession.createTopic(_config.getDestination());

		_topicPublisher = _topicSession.createPublisher(_topic);
		_topicSubscriber = _topicSession.createDurableSubscriber(_topic,
				_config.getMediaId(), getSelector(), true);

		_topicSubscriber.setMessageListener(this);
		_topicConnection.setExceptionListener(this);
		_topicConnection.start();
	}

	private String getSelector() {
		String id = _config.getMediaId();
		String broadcast = id.substring(0, 8) + "00000";
		return "(MEDIA_ID='" + id + "')OR(MEDIA_ID='" + broadcast + "')";
	}

	private void dispose() throws Exception {
		_topicConnection.close();
		_topicPublisher = null;
		_topicSubscriber = null;
		_topic = null;
		_topicSession = null;
		_topicConnection = null;
	}

	public void start() {
		_log.info("Start Media JMS");
		_manager.setOpening();
		_target = new Thread(this, MediaJMSImpl.class.getName());
		_shouldRun = true;
		_target.start();

	}

	public void stop() {
		_log.info("Stop Media JMS");
		_shouldRun = false;
		_target.interrupt();
	}

	public void run() {
		while (_shouldRun) {
			try {
				_manager.execute();
			} catch (Exception e) {
				_log.debug(e.getMessage());
			}
		}

		try {
			closingCallback();
		} catch (Exception e) {
			_log.debug(e.getMessage());
		}
	}

	public byte[] getMessage(BytesMessage message) throws JMSException {
		int lenght = (int) message.getBodyLength();
		byte[] buffer = new byte[lenght];
		message.readBytes(buffer);
		return buffer;
	}

	public static void setMessage(byte[] data, BytesMessage message)
			throws JMSException {
		message.writeBytes(data);
	}

	public void onMessage(Message message) {

		try {
			if (message instanceof BytesMessage) {
				_log.debug("Reception du message : "
						+ ((BytesMessage) message).toString());
				byte[] data = getMessage((BytesMessage) message);

				_log.debug("Reception du message : " + toHexaString(data));

				Dictionary header = new Hashtable();
				if (_messenger != null) {
					_messenger.receive(header, data);
				} else {
					_log.warn("Messenger non initialise");
				}
			}
			if (message instanceof TextMessage) {
				_log.info("Reception du message : " + message.toString());
			}

		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

	}

	public void onException(JMSException e) {
		if (_manager.isOpen()) {
			_manager.setClosing();
		}
	}

	public String getMediaId() {
		return _config.getMediaId();
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		if (!_manager.isOpen())
			throw new Exception("Media JMS not open!");

		BytesMessage message = _topicSession.createBytesMessage();
		message.writeBytes(data);

		header.put("MEDIA_ORIGIN_ID", _config.getMediaId());

		Enumeration e = header.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			message.setObjectProperty(key, header.get(key));
		}

		_log.debug("Emission du message : " + toHexaString(data));
		_topicPublisher.send(message);
	}

	public void openingCallback() throws Exception {

		int pingPeriod = _config.getPingPeriod().intValue();

		try {
			initialize();
			_openingCallbackCounter = 0;
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {

			if (_openingCallbackCounter < 3) {
				Thread.sleep(1000);
			} else if (_openingCallbackCounter < 10) {
				Thread.sleep(pingPeriod / 4);
			} else {
				Thread.sleep(pingPeriod);
			}

			_openingCallbackCounter++;
			throw e;
		}
		_log.debug("Messenger Started"); //$NON-NLS-1$
		_manager.setOpened();
	}

	public void openedCallback() throws Exception {
		Thread.sleep(1000);
	}

	public void closingCallback() throws Exception {
		dispose();
		_manager.setClosed();
	}

	public void closedCallback() {
		_manager.setOpening();
	}

	private static String toHexaString(byte[] data) {
		byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	private static byte[] fromHexaString(String hexaString) {
		byte[] buffer = hexaString.getBytes();
		byte[] data = new byte[buffer.length / 2];
		for (int i = 0; i < data.length; i++) {
			int index = i * 2;
			int rValue = (buffer[i * 2] > 0x39) ? buffer[index] - 0x37
					: buffer[index] - 0x30;
			int lValue = (buffer[i * 2 + 1] > 0x39) ? buffer[index + 1] - 0x37
					: buffer[index + 1] - 0x30;
			data[i] = (byte) (((rValue << 4) & 0xF0) | (lValue & 0x0F));

		}
		return data;
	}

	public class ClosedState implements State {

		protected Mediator _media;

		public ClosedState(Mediator media) {
			_media = media;
		}

		public void execute() throws Exception {
			_media.closedCallback();
		}
	}

	public class ClosingState implements State {

		protected Mediator _media;

		public ClosingState(Mediator media) {
			_media = media;
		}

		public void execute() throws Exception {
			_media.closingCallback();
		}
	}

	public class OpenedState implements State {

		protected Mediator _media;

		public OpenedState(Mediator media) {
			_media = media;
		}

		public void execute() throws Exception {
			_media.openedCallback();
		}
	}

	public class OpeningState implements State {

		protected Mediator _media;

		public OpeningState(Mediator media) {
			_media = media;
		}

		public void execute() throws Exception {
			_media.openingCallback();
		}
	}

	public class StateManager implements State {

		private State _currentState;

		private OpeningState _opening;

		private OpenedState _opened;

		private ClosingState _closing;

		private ClosedState _closed;

		private Mediator _mediator;

		public StateManager(Mediator mediator) {
			_mediator = mediator;
			_opening = new OpeningState(mediator);
			_opened = new OpenedState(mediator);
			_closing = new ClosingState(mediator);
			_closed = new ClosedState(mediator);
		}

		public synchronized void setOpening() {
			_currentState = _opening;
		}

		public synchronized void setOpened() {
			_currentState = _opened;

		}

		public synchronized void setClosing() {
			_currentState = _closing;
			synchronized (_mediator) {
				_mediator.notify();
			}
		}

		public synchronized void setClosed() {
			_currentState = _closed;
		}

		public void execute() throws Exception {
			_currentState.execute();
		}

		public synchronized boolean isOpen() {
			return (_currentState == _opened);
		}
	}

	public void setMessenger(MediaListener messenger) {
		_log.debug("Initialisation du messenger " + messenger);
		_messenger = messenger;
	}

}