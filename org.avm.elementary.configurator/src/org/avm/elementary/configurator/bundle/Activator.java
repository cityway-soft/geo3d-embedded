package org.avm.elementary.configurator.bundle;

import org.avm.elementary.common.AbstractActivator;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
	}

	protected void stop(ComponentContext context) {
		disposeCommandGroup();
		disposeConfiguration();
	}

	// config
	private void initializeConfiguration() {

		_cm = (ConfigurationAdmin) _context.locateService("cm");

		try {
			_config = new ConfigImpl(_context, _cm);
			_config.start();

		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	private void disposeConfiguration() {
		_config.stop();

	}

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}
}
