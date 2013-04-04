package org.avm.business.ecall.bundle;

import java.util.List;

import org.avm.business.ecall.EcallService;
import org.avm.business.ecall.EcallServiceImpl;
import org.avm.device.phony.Phony;
import org.avm.device.phony.PhonyInjector;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class Activator extends AbstractActivator implements EcallService, PhonyInjector, JDBInjector {
	private ConfigurationAdmin _cm;

	private EcallServiceImpl _peer;

	private ConsumerImpl _consumer;

	private ProducerImpl _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;
	
	private static AbstractActivator _plugin;

	public Activator() {
		_plugin = this;
		_peer = new EcallServiceImpl();
	}

	public static AbstractActivator getDefault() {
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
		disposeProducer();
		disposeConsumer();
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
		AlarmService alarmeService = (AlarmService) _context.locateService("alarm");
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

	public boolean ack(String phone) {
		return _peer.ack(phone);
	}

	public boolean endEcall() {
		return _peer.endEcall();
	}

	public boolean startEcall() {
		return _peer.startEcall();
	}

	public List getAlarm() {
		return _peer.getAlarm();
	}

	public String getProducerPID() {
		return _peer.getProducerPID();
	}

	public State getState() {
		return _peer.getState();
	}

	public void setPhony(Phony phony) {
		_peer.setPhony(phony);
	}

	public void unsetPhony(Phony phony) {
		_peer.unsetPhony(phony);
	}

	public void setJdb(JDB jdb) {
		_peer.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_peer.unsetJdb(jdb);
	}

}
