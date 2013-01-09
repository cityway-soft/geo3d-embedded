package org.avm.elementary.database.bundle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.database.Database;
import org.avm.elementary.database.impl.DatabaseImpl;
import org.avm.elementary.database.impl.DatabaseManager;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Database {
	
	private static Activator _plugin;
	private ConfigurationAdmin _cm;
	private DatabaseImpl _peer;
	private DatabaseManager _manager;
	private ConfigImpl _config;
	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
		_peer = new DatabaseImpl();
	}
	
	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		initializeManager();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeManager();
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

	// manager
	private void initializeManager() {
		try {
			_manager = new DatabaseManager(_context.getBundleContext(), _config);
			_manager.start();
		} catch (Exception e) {
			_log.error(e.getMessage());
		}
	}

	private void disposeManager() {
		if (_manager != null)
			_manager.stop();
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

	public Connection getConnection() throws SQLException {
		return _peer.getConnection();
	}

	public void releaseConnection(Connection conn) throws SQLException {
		_peer.releaseConnection(conn);
	}

	public PreparedStatement preparedStatement(String query)
			throws SQLException {
		return _peer.preparedStatement(query);
	}

	public void releasePreparedStatement(PreparedStatement query)
			throws SQLException {
		_peer.releasePreparedStatement(query);
	}

	public ResultSet sql(String sql) {
		return _peer.sql(sql);
	}

	public String getVersion() {
		return _peer.getVersion();
	}

}
