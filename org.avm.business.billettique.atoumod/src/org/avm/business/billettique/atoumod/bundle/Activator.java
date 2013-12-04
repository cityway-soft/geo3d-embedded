package org.avm.business.billettique.atoumod.bundle;

import org.avm.business.billettique.atoumod.Billettique;
import org.avm.business.billettique.atoumod.BillettiqueImpl;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Billettique,
		AvmInjector {
	private static Activator _plugin;
	private ConfigImpl _config;
	private ConsumerImpl _consumer;
	private CommandGroupImpl _commands;
	private BillettiqueImpl _peer;
	private org.avm.business.billettique.atoumod.bundle.ProducerImpl _producer;

	public Activator() {
		_plugin = this;
		_peer = new BillettiqueImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeConsumer();
		initializeProducer();
		initializeCommandGroup();
		initializeAlarmService();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeAlarmService();
		disposeCommandGroup();
		disposeConsumer();
		disposeProducer();
		disposeConfiguration();
	}

	// config
	private void initializeConfiguration() {
		ConfigurationAdmin cm = (ConfigurationAdmin) _context
				.locateService("cm");
		try {
			_config = new ConfigImpl(_context, cm);
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

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}

	// alarm service
	private void initializeAlarmService() {
		AlarmService alarmeService = (AlarmService) _context
				.locateService("alarm");
		_peer.setAlarmService(alarmeService);
	}

	private void disposeAlarmService() {
		_peer.setAlarmService(null);
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

	public void setAvm(org.avm.business.core.Avm avm) {
		_log.debug("setAvm " + avm);
		_peer.setAvm(avm);
	}

	public void unsetAvm(Avm avm) {
		_peer.unsetAvm(null);
	}

	public void setEnable(boolean b) {
		_peer.setEnable(b);
	}
	
	public void setJdb(JDB jdb) {
		_log.debug("setJdb = " + jdb);
		_peer.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_log.debug("unsetJdb");
		_peer.unsetJdb(null);
	}

}
