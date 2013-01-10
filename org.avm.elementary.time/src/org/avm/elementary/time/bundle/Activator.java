package org.avm.elementary.time.bundle;

import org.avm.device.gps.Gps;
import org.avm.device.gps.GpsInjector;
import org.avm.device.timemanager.TimeManager;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.time.TimeManagerImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements TimeManager,
		GpsInjector {

	private static org.avm.elementary.time.bundle.Activator _plugin;

	private ConfigurationAdmin _cm;

	private TimeManager _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
		_peer = new TimeManagerImpl();
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
	}

	private void startService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public boolean update() {
		return _peer.update();
	}

	public void setGps(Gps gps) {
		((GpsInjector) _peer).setGps(gps);
	}

	public void unsetGps(Gps gps) {
		((GpsInjector) _peer).unsetGps(gps);
	}
}
