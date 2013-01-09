package org.avm.device.generic.can.manager.bundle;

import org.avm.device.generic.can.manager.CanManagerImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ComponentFactoryInjector;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;

public class Activator extends AbstractActivator {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private CanManagerImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private ComponentFactory _factory;

	public Activator() {
		_peer = new CanManagerImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		initializeComponentFactory();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeComponentFactory();
		disposeCommandGroup();
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

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}

	// component factory
	private void initializeComponentFactory() {
		_factory = (ComponentFactory) _context.locateService("factory");
		if (_peer instanceof ComponentFactoryInjector) {
			((ComponentFactoryInjector) _peer).setComponentFactory(null,_factory);
		}
	}

	private void disposeComponentFactory() {
		if (_peer instanceof ComponentFactoryInjector) {
			((ComponentFactoryInjector) _peer).unsetComponentFactory(null,null);
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

}
