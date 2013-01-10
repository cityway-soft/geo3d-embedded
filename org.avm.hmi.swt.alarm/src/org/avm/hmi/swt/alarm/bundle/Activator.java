package org.avm.hmi.swt.alarm.bundle;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.alarm.AlarmIhm;
import org.avm.hmi.swt.alarm.AlarmImpl;
import org.avm.hmi.swt.desktop.Desktop;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements AlarmIhm, UserSessionServiceInjector {

	static final String PID = AlarmIhm.class.getName();

	private ConfigurationAdmin _cm;

	private AlarmImpl _peer;

	private ConsumerImpl _consumer;

	private org.avm.hmi.swt.alarm.bundle.ProducerImpl _producer;

	private org.avm.hmi.swt.alarm.bundle.ConfigImpl _config;

	private org.avm.hmi.swt.alarm.bundle.CommandGroupImpl _commands;

	private static AbstractActivator _plugin;

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_peer = new AlarmImpl();
	}

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		initializeContext();
		initializeBaseIhm();
		initializeProducer();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeProducer();
		disposeBaseIhm();
		disposeContext();
		disposeCommandGroup();
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
	
	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}
	
	private void initializeContext(){
		_peer.setContext(_context);
	}
	
	private void disposeContext(){
		_peer.setContext(null);
	}
	
	// Desktop
	private void initializeBaseIhm() {
		Desktop base = (Desktop) _context.locateService("desktop");
		_peer.setBase(base);
	}

	private void disposeBaseIhm() {
		_peer.setBase(null);
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

	public void start() {
		_peer.start();
	}

	public void stop() {
		_peer.stop();
	}

	public List getAlarm() {
		return _peer.getAlarm();
	}

	public String getProducerPID() {
		return _peer.getProducerPID();
	}

	public void setUserSessionService(UserSessionService service) {
		_peer.setUserSessionService(service);
	}

	public void unsetUserSessionService(UserSessionService service) {
		_peer.unsetUserSessionService(service);
	}

}
