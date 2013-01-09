package org.avm.elementary.database.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.database.Database;
import org.avm.elementary.database.bundle.Activator;
import org.hsqldb.jdbcDriver;

/**
 * @author root
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class DatabaseImpl implements Database, ConfigurableService,
		ManageableService {

	private Logger _log = Activator.getDefault().getLogger();

	private DatabaseConfig _config;

	private Driver _driver;

	private Properties _info;

	private boolean _isOpened = false;

	public DatabaseImpl() {
		// _log.setPriority(Priority.DEBUG);
	}

	public String toString() {
		return " TODO get current version";
	}

	public void start() {
		_isOpened = true;
		_log.info("Starting database" + toString());
	}

	public void stop() {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute("SHUTDOWN");
			releaseConnection(conn);
		} catch (SQLException e) {
			_log.error(e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					releaseConnection(conn);
			} catch (SQLException e) {
				_log.error(e);
			}

			_isOpened = false;
			_driver = null;
			_log.debug("Components stopped");
		}

	}

	public Connection getConnection() throws SQLException {

		Object[] arguments = { System.getProperty("org.avm.home") };
		String text = MessageFormat.format(_config.getUrlConnection(),
				arguments);

		_log.debug("connecting : url = " + text + " user = "
				+ _config.getLogin() + " password = " + _config.getPassword());

		if (!_isOpened) {
			throw new SQLException("[DSU] database closed ............ !");
		}

		Driver driver = getDriver();
		Properties info = getPropertiesInfo();
		_log.debug("Propriete de la base : " + info + "V"
				+ driver.getMajorVersion() + "." + driver.getMinorVersion());
		Connection conn = _driver.connect(text, info);

		_log.debug("connected : " + conn);
		return conn;
	}

	public void releaseConnection(Connection conn) throws SQLException {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			_log.error(e.getMessage(), e);
		}
	}

	public PreparedStatement preparedStatement(String query)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void releasePreparedStatement(PreparedStatement query)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public ResultSet sql(String sql) {

		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			_log.info("Execute " + sql);
			long t0 = System.currentTimeMillis();
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			long ms = System.currentTimeMillis() - t0;
			_log.info("fin query (" + ms + "ms)");

		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					releaseConnection(conn);
			} catch (SQLException e) {
				_log.error(e.getMessage(), e);
			}
		}
		return rs;
	}

	public void configure(Config config) {
		_config = (DatabaseConfig) config;
		_info = null;
	}

	private Properties getPropertiesInfo() {
		if (_info == null) {
			Properties info = new Properties();
			info.put("user", _config.getLogin());
			info.put("password", _config.getPassword());
			info.put("ifexists", "true");
			_info = info;
		}
		return _info;
	}

	private Driver getDriver() {
		if (_driver == null) {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat.format(_config.getUrlConnection(),
					arguments);
			String tmp = new String(text);
			String lckFile = tmp.substring(tmp.lastIndexOf(':') + 1) + ".lck";
			File fd = new File(lckFile);
			if (fd.exists()) {
				_log.warn("[DSU] remove lck file " + lckFile);
				fd.delete();
			}
			_driver = new jdbcDriver();
		}
		return _driver;
	}

	public String getVersion() {
		return _config.getVersion();
	}

}