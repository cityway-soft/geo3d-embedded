package org.avm.hmi.swt.management.bundle;

import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.management.Management;
import org.avm.hmi.swt.management.ManagementImpl;
import org.knopflerfish.service.console.ConsoleService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Management,
		UserSessionServiceInjector {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private ManagementImpl _peer;

	private ConsumerImpl _consumer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	public Activator() {
		_peer = new ManagementImpl();
		_plugin = this;
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		initializeBundleContext(context);
		initializeConsoleService();
		initializeDesktop();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeDesktop();
		disposeConsoleService();
		disposeBundleContext();
		disposeCommandGroup();
		disposeConfiguration();
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

		_log.debug("dispose consumer");
		if (_peer instanceof ConsumerService) {
			_consumer.stop();
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

	// Base
	private void initializeDesktop() {
		Desktop base = (Desktop) _context.locateService("desktop");
		_peer.setDesktop(base);
	}

	private void disposeDesktop() {
		_peer.setDesktop(null);
	}

	// BundleContext
	private void initializeBundleContext(ComponentContext context) {
		_peer.setBundleContext(context.getBundleContext());
	}

	private void disposeBundleContext() {
		_peer.setBundleContext(null);
	}

	// console
	private void initializeConsoleService() {
		ConsoleService console = (ConsoleService) _context
				.locateService("console");
		_peer.setConsoleService(console);
	}

	private void disposeConsoleService() {
		_peer.setConsoleService(null);
	}

	// service
	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
	}

	private void startService() {
		_log.debug("startService");
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public void setUserSessionService(UserSessionService service) {
		_peer.setUserSessionService(service);
	}

	public void unsetUserSessionService(UserSessionService service) {
		_peer.unsetUserSessionService(service);
	}

}
