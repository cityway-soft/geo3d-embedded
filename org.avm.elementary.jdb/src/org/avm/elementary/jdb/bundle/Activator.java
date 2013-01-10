package org.avm.elementary.jdb.bundle;

import java.util.Date;

import org.avm.device.gps.Gps;
import org.avm.device.gps.GpsInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.impl.JDBImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements JDB, GpsInjector {

	static final String PID = JDB.class.getName();

	private ConfigurationAdmin _cm;

	private JDBImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	public Activator() {
		_peer = new JDBImpl();
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

	public void journalize(String category, String message) {
		_peer.journalize(category, message);
	}

	public void sync() {
		_peer.sync();

	}

	public int getCheckPeriod() {
		return _peer.getCheckPeriod();
	}

	public String getScheduledFilename(Date date) {
		return _peer.getScheduledFilename(date);
	}

	public String getRootPath() {
		return _peer.getRootPath();
	}

	public void setGps(Gps gps) {
		((GpsInjector) _peer).setGps(gps);
	}

	public void unsetGps(Gps gps) {
		((GpsInjector) _peer).unsetGps(gps);
	}
}
