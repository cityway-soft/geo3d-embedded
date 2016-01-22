package org.avm.elementary.alarm.bundle;

import java.util.Collection;

import org.avm.business.core.Avm;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.alarm.impl.AlarmServiceImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements AlarmService {

	private static org.avm.elementary.alarm.bundle.Activator _plugin;

	private ConfigurationAdmin _cm;

	private AlarmServiceImpl _peer;

	private ConsumerImpl _consumer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private org.avm.elementary.alarm.bundle.ProducerImpl _producer;

	public Activator() {
		_plugin = this;
		_peer = new AlarmServiceImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeMessenger();
		initializeCommandGroup();
		initializeProducer();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeConsumer();
		disposeProducer();
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
		if (_peer instanceof MessengerInjector) {
			Messenger messenger = (Messenger) _context
					.locateService("messenger");
			((MessengerInjector) _peer).setMessenger(messenger);
		}
	}

	private void disposeMessenger() {
		if (_peer instanceof MessengerInjector) {
			((MessengerInjector) _peer).setMessenger(null);
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

	public boolean isAlarm() {
		return _peer.isAlarm();
	}

	public Collection getList() {
		return _peer.getList();
	}

	public Alarm getAlarm(Integer id) {
		return _peer.getAlarm(id);
	}

	public void setJdb(JDB jdb) {
		_log.debug("setJdb = " + jdb);
		_peer.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_log.debug("unsetJdb");
		_peer.unsetJdb(null);
	}

	public Alarm getAlarmByKey(String name) {
		return _peer.getAlarmByKey(name);
	}

	public void setAvm(Avm avm) {
		_peer.setAvm(avm);
	}

	public void unsetAvm(Avm avm) {
		_peer.unsetAvm(null);
	}

}
