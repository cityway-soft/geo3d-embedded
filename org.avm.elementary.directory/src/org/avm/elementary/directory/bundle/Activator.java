package org.avm.elementary.directory.bundle;

import java.util.Properties;

import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.directory.Directory;
import org.avm.elementary.directory.impl.DirectoryImpl;
import org.avm.elementary.directory.impl.DirectoryManager;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Directory {

	static final String PID = Directory.class.getName();

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private final Directory _peer;

	private static Activator _plugin;

	private DirectoryManager _manager;

	public Activator() {
		_peer = new DirectoryImpl();
		_plugin = this;
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(final ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		initializeManager();
	}

	protected void stop(final ComponentContext context) {
		disposeCommandGroup();
		disposeConfiguration();
		disposeManager();
	}

	// manager
	private void initializeManager() {
		try {
			_manager = new DirectoryManager(_context.getBundleContext(),
					_config);
			_manager.start();
		} catch (final Exception e) {
			_log.error(e.getMessage());
		}
	}

	private void disposeManager() {
		if (_manager != null) {
			_manager.stop();
		}
	}

	// config
	private void initializeConfiguration() {

		final ConfigurationAdmin cm = (ConfigurationAdmin) _context
				.locateService("cm");
		try {
			_config = new ConfigImpl(_context, cm);
			_config.start();
			if (_peer instanceof ConfigurableService) {
				((ConfigurableService) _peer).configure(_config);

			}
		} catch (final Exception e) {
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
		if (_commands != null) {
			_commands.stop();
		}
	}

	public Properties getProperty(final String key) {
		return _peer.getProperty(key);
	}

}
