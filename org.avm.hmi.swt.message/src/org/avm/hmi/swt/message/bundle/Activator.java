package org.avm.hmi.swt.message.bundle;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.business.messages.Messages;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.message.MessageIhm;
import org.avm.hmi.swt.message.MessageImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements MessageIhm, UserSessionServiceInjector {

	static final String PID = MessageIhm.class.getName();

	private ConfigurationAdmin _cm;

	private MessageImpl _peer;

	private ConsumerImpl _consumer;

	private org.avm.hmi.swt.message.bundle.ConfigImpl _config;

	private org.avm.hmi.swt.message.bundle.CommandGroupImpl _commands;

	private static AbstractActivator _plugin;

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_peer = new MessageImpl();
	}

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();

		initializeBaseIhm();
		initializeMessenger();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeMessenger();
		disposeBaseIhm();
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
	
	
	// Desktop
	private void initializeBaseIhm() {
		Desktop base = (Desktop) _context.locateService("desktop");
		_peer.setDesktop(base);
	}

	private void disposeBaseIhm() {
		_peer.setDesktop(null);
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
	
	// messenger
	private void initializeMessenger() {
		Messenger messenger = (Messenger) _context.locateService("messenger");
		_peer.setMessenger(messenger);
	}

	private void disposeMessenger() {
		_peer.setMessenger(null);
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

	public String getProducerPID() {
		return _peer.getProducerPID();
	}

	public void setUserSessionService(UserSessionService service) {
		_peer.setUserSessionService(service);
	}

	public void unsetUserSessionService(UserSessionService service) {
		_peer.unsetUserSessionService(service);
	}
	
	public void setMessages(Messages messages) {
		_peer.setMessages(messages);
	}

	public void unsetMessages(Messages messages) {
		_peer.unsetMessages(null);
	}

}
