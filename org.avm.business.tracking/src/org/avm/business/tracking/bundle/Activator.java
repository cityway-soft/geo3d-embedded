package org.avm.business.tracking.bundle;

import org.avm.business.tracking.Tracking;
import org.avm.business.tracking.TrackingImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Tracking,
		MessengerInjector {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private TrackingImpl _peer;

	private ConfigImpl _config;

	private ConsumerImpl _consumer;

	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
		_peer = new TrackingImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeCommandGroup();
		disposeConfiguration();
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

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}

	public void setMessenger(Messenger messenger) {
		_peer.setMessenger(messenger);
	}

	public void unsetMessenger(Messenger messenger) {
		_peer.unsetMessenger(messenger);
	}

	public void setFrequency(int freq) {
		_peer.setFrequency(freq);
	}

	public void configurationEvent(ConfigurationEvent event) {

		if (true) {
			System.out.println();
		}
	}

}
