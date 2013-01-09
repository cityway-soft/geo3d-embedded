package org.avm.elementary.variable.bundle;

import org.apache.log4j.Logger;
import org.avm.elementary.common.ComponentFactoryInjector;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.variable.VariableService;
import org.avm.elementary.variable.VariableServiceImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.util.measurement.Measurement;

public class Activator implements VariableService {

	static final String PID = VariableService.class.getName();

	private Logger _log = Logger.getInstance(this.getClass());

	private ComponentContext _context;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private VariableService _peer;

	public Activator() {
		_peer = new VariableServiceImpl();
	}

	protected void activate(ComponentContext context) {
		_log.info("Components activated");
		_context = context;
		initializeConfiguration();
		initializeCommandGroup();
		initializeComponentFactory();
		startService();
	}

	protected void deactivate(ComponentContext context) {
		_log.info("Component deactivated");
		stopService();
		disposeComponentFactory();
		disposeCommandGroup();
		disposeConfiguration();
	}

	// component factory
	private void initializeComponentFactory() {
		if (_peer instanceof ComponentFactoryInjector) {
			ComponentFactoryInjector injector = (ComponentFactoryInjector) _peer;

			injector
					.setComponentFactory(
							"org.avm.elementary.variable.DigitalVariable",
							(ComponentFactory) _context
									.locateService("org.avm.elementary.variable.DigitalVariable"));
			injector
					.setComponentFactory(
							"org.avm.elementary.variable.AnalogVariable",
							(ComponentFactory) _context
									.locateService("org.avm.elementary.variable.AnalogVariable"));
			injector
					.setComponentFactory(
							"org.avm.elementary.variable.CounterVariable",
							(ComponentFactory) _context
									.locateService("org.avm.elementary.variable.CounterVariable"));
		}
	}

	private void disposeComponentFactory() {
		if (_peer instanceof ComponentFactoryInjector) {
			ComponentFactoryInjector injector = (ComponentFactoryInjector) _peer;

			injector
					.unsetComponentFactory(
							"org.avm.elementary.variable.DigitalVariable",
							(ComponentFactory) _context
									.locateService("org.avm.elementary.variable.DigitalVariable"));
			injector
					.unsetComponentFactory(
							"org.avm.elementary.variable.AnalogVariable",
							(ComponentFactory) _context
									.locateService("org.avm.elementary.variable.AnalogVariable"));
			injector
					.unsetComponentFactory(
							"org.avm.elementary.variable.CounterVariable",
							(ComponentFactory) _context
									.locateService("org.avm.elementary.variable.CounterVariable"));
		}
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

	public Measurement read(String name) {
		return _peer.read(name);
	}

	public void write(String name, Measurement value) {
		_peer.write(name, value);
	}

}
