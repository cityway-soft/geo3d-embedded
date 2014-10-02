package org.avm.elementary.fonts.bundle;

import org.apache.log4j.Logger;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.AbstractCommandGroup;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator {

	private static Activator plugin;
	private Logger log;
	private ConfigImpl config;
	private FontsManager manager;
	private AbstractCommandGroup commands;

	public Activator() {
		plugin = this;
		log = Logger.getInstance(this.getClass());
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeManager();
		initializeCommandGroup();
	}

	protected void stop(ComponentContext context) {
		disposeCommandGroup();
		disposeManager();
		disposeConfiguration();
	}

	public static Activator getDefault() {
		return plugin;
	}

	// config
	private void initializeConfiguration() {
		ConfigurationAdmin cm = (ConfigurationAdmin) _context
				.locateService("cm");
		try {
			config = new ConfigImpl(_context, cm);
			config.start();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void disposeConfiguration() {
		config.stop();
	}
	
	protected void initializeManager() {
		try {
			manager = new FontsManager(_context.getBundleContext(), config);
			manager.start();
			
		} catch (final Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	private void disposeManager() {
		if (manager != null) {
			manager.stop();
		}
	}
	
	// commands
		private void initializeCommandGroup() {
			commands = new CommandGroupImpl(_context, config);
			commands.start();
		}

		private void disposeCommandGroup() {
			if (commands != null)
				commands.stop();
		}
}
