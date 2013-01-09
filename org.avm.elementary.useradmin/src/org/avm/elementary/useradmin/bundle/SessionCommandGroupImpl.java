package org.avm.elementary.useradmin.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.manager.UserAdminManagerConfig;
import org.avm.elementary.useradmin.session.UserSessionServiceImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class SessionCommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "usersession";

	private UserSessionService _peer;

	public SessionCommandGroupImpl(ComponentContext context,
			UserSessionService peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for user login/logout.");
		_peer = peer;
	}

	// Login
	public final static String USAGE_LOGIN = "-u #user# [-p #pwd#]";

	public final static String[] HELP_LOGIN = new String[] { "Login", };

	public int cmdLogin(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String suser = ((String) opts.get("-u")).trim();
		String spwd = ((String) opts.get("-p"));
		spwd = (spwd == null) ? "" : spwd.trim();
		try {
			_peer.login(suser, spwd);
		} catch (Throwable t) {
			out.println("ERROR: " + t.getMessage());
		}

		return 0;
	}

	// Authentication
	public final static String USAGE_AUTHENTICATION = "[<auth>]";

	public final static String[] HELP_AUTHENTICATION = new String[] { "Set authentication mode : default is true ", };

	public int cmdAuthentication(Dictionary opts, Reader in, PrintWriter out,
			Session sessionlogin) {
		String auth = ((String) opts.get("auth"));
		if (auth != null) {
			boolean state = auth.toLowerCase().equals("true");
			((UserAdminManagerConfig) _config).setAuthentication(state);
			((UserSessionServiceImpl) _peer).setAuthentification(state);
			_config.updateConfig(false);
		}

		out.println("Authentication mode : "
				+ ((UserAdminManagerConfig) _config).isAuthentication());
		return 0;
	}

	// Logout
	public final static String USAGE_LOGOUT = "";

	public final static String[] HELP_LOGOUT = new String[] { "Logout", };

	public int cmdLogout(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			_peer.logout();
		} catch (Throwable t) {
			out.println("ERROR: " + t.getMessage());
		}

		return 0;
	}

	// Logout
	public final static String USAGE_WHO = "";

	public final static String[] HELP_WHO = new String[] { "Who is logged on ?", };

	public int cmdWho(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			Dictionary dic = _peer.getUserProperties();
			if (dic != null) {
				Enumeration e = dic.elements();
				while (e.hasMoreElements()) {
					out.println("-" + e.nextElement());
				}
			} else {
				out.println("nobody logged on.");
			}
		} catch (Throwable t) {
			out.println("ERROR: " + t.getMessage());
		}

		return 0;
	}

}
