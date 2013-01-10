package org.avm.elementary.database.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.database.Database;
import org.avm.elementary.database.impl.DatabaseConfig;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "database";

	private Database _peer;

	CommandGroupImpl(ComponentContext context, Database peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the database.");
		_peer = peer;
	}

	// URL connection
	public final static String USAGE_SETURLCONNECTION = "<uri>";

	public final static String[] HELP_SETURLCONNECTION = new String[] { "Set URL connection", };

	public int cmdSeturlconnection(Dictionary opts, Reader in, PrintWriter out,
			Session sessionlogin) {
		String uri = ((String) opts.get("uri")).trim();
		((DatabaseConfig) _config).setUrlConnection(uri);
		_config.updateConfig();

		out.println("Current URL connection : "
				+ ((DatabaseConfig) _config).getUrlConnection());
		return 0;
	}

	public final static String USAGE_SHOWURLCONNECTION = "";

	public final static String[] HELP_SHOWURLCONNECTION = new String[] { "Show current URL connection", };

	public int cmdShowurlconnection(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current URL connection : "
				+ ((DatabaseConfig) _config).getUrlConnection());
		return 0;
	}

	// login
	public final static String USAGE_SETLOGIN = "<login>";

	public final static String[] HELP_SETLOGIN = new String[] { "Set login", };

	public int cmdSetlogin(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String login = ((String) opts.get("login")).trim();
		((DatabaseConfig) _config).setLogin(login);
		_config.updateConfig();

		out.println("Current login : " + ((DatabaseConfig) _config).getLogin());
		return 0;
	}

	public final static String USAGE_SHOWLOGIN = "";

	public final static String[] HELP_SHOWLOGIN = new String[] { "Show current login", };

	public int cmdShowlogin(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current login : " + ((DatabaseConfig) _config).getLogin());
		return 0;
	}

	// password
	public final static String USAGE_SETPASSWORD = "<password>";

	public final static String[] HELP_SETPASSWORD = new String[] { "Set password", };

	public int cmdSetpassword(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String password = ((String) opts.get("password")).trim();
		((DatabaseConfig) _config).setPassword(password);
		_config.updateConfig();

		out.println("Current password : "
				+ ((DatabaseConfig) _config).getLogin());
		return 0;
	}

	public final static String USAGE_SHOWPASSWORD = "";

	public final static String[] HELP_SHOWPASSWORD = new String[] { "Show current password", };

	public int cmdShowpassword(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current password : "
				+ ((DatabaseConfig) _config).getPassword());
		return 0;
	}

	// SQL
	public final static String USAGE_SQL = "<request>";

	public final static String[] HELP_SQL = new String[] { "Request database" };

	public int cmdSql(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String sql = ((String) opts.get("request")).trim();
		ResultSet rs = _peer.sql(sql);
		debug(rs, out);
		return 0;
	}

	private void debug(ResultSet rs, PrintWriter out) {
		if (rs != null) {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int colnum = rsmd.getColumnCount();

				StringBuffer buf;

				buf = new StringBuffer();
				for (int i = 1; i <= colnum; i++) {
					buf.append("| ");
					buf.append(rsmd.getColumnName(i));
					buf.append("| \t");
				}
				out.println(buf);

				while (rs.next()) {
					buf = new StringBuffer();
					for (int i = 1; i <= colnum; i++) {
						buf.append("| ");
						buf.append(rs.getString(i));
						buf.append("\t");
					}
					out.println(buf);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
