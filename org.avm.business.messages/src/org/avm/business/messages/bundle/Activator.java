package org.avm.business.messages.bundle;

import java.util.Collection;

import org.avm.business.messages.Messages;
import org.avm.business.messages.MessagesImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Messages {

	private static Activator _plugin;
	private ConfigImpl _config;
	private CommandGroupImpl _commands;
	private MessagesImpl _peer;
	private ConsumerImpl _consumer;
	private ProducerImpl _producer;

	public Activator() {
		_plugin = this;
		_peer = new MessagesImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
			initializeConfiguration();
			initializeCommandGroup();
			initializeConsumer();
			initializeProducer();
			initializeMessenger();
			startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeMessenger();
		disposeProducer();
		disposeConsumer();
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

	// messenger
	private void initializeMessenger() {
		if (_peer instanceof MessengerInjector) {
			Messenger messenger = (Messenger) _context
					.locateService("messenger");
			_peer.setMessenger(messenger);
		}

	}

	private void disposeMessenger() {
		_peer.setMessenger(null);
	}

	public Collection getMessages(int destinataire, String affectation) {
		return _peer.getMessages(destinataire, affectation);
	}

	public void acquittement(String msgId) {
		_peer.acquittement(msgId);
	}
}
