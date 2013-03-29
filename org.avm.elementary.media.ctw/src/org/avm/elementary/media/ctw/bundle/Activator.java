package org.avm.elementary.media.ctw.bundle;

import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Media;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.common.PublisherService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.media.ctw.MediaCTW;
import org.avm.elementary.media.ctw.MediaCTWImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Media,
		AlarmProvider {
	public static final String PID = MediaCTW.class.getName();

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private MediaCTWImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private MediaListener _messenger;

	private ConsumerImpl _consumer;

	private JDBProxy _jdb;

	private ProducerImpl _producer;

	public Activator() {
		_plugin = this;
		_peer = new MediaCTWImpl();
		_jdb = new JDBProxy();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeMessenger();
		initializeCommandGroup();
		initializeConsumer();
		initializeJDB();
		initializeProducer();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeProducer();
		disposeJDB();
		disposeConsumer();
		disposeCommandGroup();
		disposeMessenger();
		disposeConfiguration();
	}

	// config
	private void initializeConfiguration() {

		_cm = (ConfigurationAdmin) _context.locateService("cm");
		try {
			_config = new ConfigImpl(_context, _cm);
			_config.start();
			if (_peer instanceof ConfigurableService) {
				((ConfigurableService) _peer).configure(_config);

			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

	}

	private void disposeConfiguration() {
		_config.stop();
		if (_peer instanceof ConfigurableService) {
			((ConfigurableService) _peer).configure(null);
		}
	}

	// producer
	private void initializeProducer() {
		if (_peer instanceof ProducerService) {
			_producer = new ProducerImpl(_context);
			_producer.start();
			((ProducerService) _peer).setProducer(_producer);
		}
	}

	private void disposeProducer() {
		if (_peer instanceof ProducerService) {
			((ProducerService) _peer).setProducer(null);
			_producer.stop();
		}
	}

	// messenger
	private void initializeMessenger() {
		_messenger = (MediaListener) _context.locateService("messenger");
		if (_peer instanceof PublisherService) {
			_peer.setMessenger(_messenger);
		}
	}

	private void disposeMessenger() {
		if (_peer instanceof PublisherService) {
			_peer.setMessenger(null);
		}
	}

	// consumer
	private void initializeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer = new ConsumerImpl(_context, _peer);
			_consumer.start();
		}
	}

	private void disposeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer.stop();
		}
	}

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}

	// service
	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
	}

	private void startService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public String getMediaId() {
		return "CTW";
	}

	public int getPriority() {
		return 4;
	}

	// jdb
	private void initializeJDB() {
		if (_peer instanceof JDBInjector)
			((JDBInjector) _peer).setJdb(_jdb);
	}

	private void disposeJDB() {
		if (_peer instanceof JDBInjector)
			((JDBInjector) _peer).setJdb(null);
	}

	public void setJdb(JDB jdb) {
		_jdb.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_jdb.unsetJdb(jdb);
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		if (_peer == null)
			return;
		_peer.send(header, data);
	}

	class JDBProxy implements JDB, JDBInjector {

		protected JDB _peer;

		public void setJdb(JDB jdb) {
			_peer = jdb;
		}

		public void unsetJdb(JDB jdb) {
			_peer = null;
		}

		public JDBProxy() {
		}

		public int getCheckPeriod() {
			if (_peer != null)
				return _peer.getCheckPeriod();
			return 0;
		}

		public String getRootPath() {
			if (_peer != null)
				return _peer.getRootPath();
			return null;
		}

		public String getScheduledFilename(Date date) {
			if (_peer != null)
				return _peer.getScheduledFilename(date);
			return null;
		}

		public void journalize(String category, String message) {
			if (_peer != null) {
				_peer.journalize(category, message);
			}
		}

		public void sync() {
			if (_peer != null) {
				_peer.sync();
			}
		}

	}

	public List getAlarm() {
		return _peer.getAlarm();
	}

	public String getProducerPID() {
		return _peer.getProducerPID();
	}

}
