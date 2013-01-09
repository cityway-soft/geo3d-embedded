package org.avm.device.generic.io.iocardbus.bundle;

import org.apache.log4j.Logger;
import org.avm.device.generic.io.iocardbus.IOCardBusImpl;
import org.avm.device.io.IOCardInfo;
import org.avm.device.io.iocardbus.IOCardBus;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements IOCardBus {

	static final String PID = IOCardBus.class.getName();

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private IOCardBusImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance("org.avm.device.io");
		_peer = new IOCardBusImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}
	
	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
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

	// service
	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
		_peer.setBundleContext(null);
	}

	private void startService() {
		_peer.setBundleContext(_context.getBundleContext());
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public void registerDevice(IOCardInfo card) {
		_peer.registerDevice(card);
	}

	public void unregisterDevice(IOCardInfo card) {
		_peer.unregisterDevice(card);
	}

}
