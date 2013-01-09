package org.angolight.hmi.leds.bundle;

import org.angolight.device.leds.LedsInjector;
import org.angolight.hmi.leds.Leds;
import org.angolight.hmi.leds.LedsImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Leds {
	private static Activator _plugin;
	private ConfigImpl _config;
	private CommandGroupImpl _commands;
	private LedsImpl _peer;
	private ConsumerImpl _consumer;

	public Activator() {
		_plugin = this;
		_peer = new LedsImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeConsumer();
		initializeCommandGroup();
		initializeLeds();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeLeds();
		disposeCommandGroup();
		disposeConsumer();
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

	// device leds
	private void initializeLeds() {
		Object obj = _context.locateService("leds");
		org.angolight.device.leds.Leds leds = (org.angolight.device.leds.Leds)obj;
		if (_peer instanceof LedsInjector)
			((LedsInjector) _peer).setLeds(leds);
	}

	private void disposeLeds() {
		if (_peer instanceof LedsInjector)
			((LedsInjector) _peer).setLeds(null);
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
		if (_peer instanceof ConsumerService) {
			_consumer.start();
		}
	}

	public int display(String state) {
		return _peer.display(state);
	}


}