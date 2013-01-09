package org.avm.elementary.useradmin.bundle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Dictionary;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.useradmin.manager.UserAdminManager;
import org.avm.elementary.useradmin.manager.UserAdminManagerConfig;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

public class ManagerCommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "usermanager";

	private UserAdminManager _peer;

	public ManagerCommandGroupImpl(ComponentContext context,
			UserAdminManager peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for useradmin manager.");
		_peer = peer;
	}



	// Set default roles
	public final static String USAGE_SETDEFAULTROLES = "[<roles>]";

	public final static String[] HELP_SETDEFAULTROLES = new String[] { "Set default roles (separated by ';'", };

	public int cmdSetdefaultroles(Dictionary opts, Reader in, PrintWriter out,
			Session sessionlogin) {
		String roles = ((String) opts.get("roles"));
		if (roles != null) {
			StringTokenizer t = new StringTokenizer(roles, ";");
			String[] values = new String[t.countTokens()];
			int i = 0;
			while (t.hasMoreElements()) {
				String role = (String) t.nextElement();
				values[i] = role;
				i++;
			}
			((UserAdminManagerConfig) _config).setDefaultRoles(values);
			_config.updateConfig();
		} else {
			StringBuffer buf = new StringBuffer();
			String[] defRoles = ((UserAdminManagerConfig) _config)
					.getDefaultRoles();
			for (int i = 0; i < defRoles.length; i++) {
				buf.append(defRoles[i]);
				buf.append(";");
			}
			out.println("Current roles : " + buf.toString());
		}
		return 0;
	}

	// Filename
	public final static String USAGE_SETFILENAME = "<filename>";

	public final static String[] HELP_SETFILENAME = new String[] { "Set file name", };

	public int cmdSetfilename(Dictionary opts, Reader in, PrintWriter out,
			Session sessionlogin) {
		String filename = ((String) opts.get("filename")).trim();
		((UserAdminManagerConfig) _config).setFileName(filename);
		_config.updateConfig();

		out.println("Current Filename : "
				+ ((UserAdminManagerConfig) _config).getFileName());
		return 0;
	}

	public final static String USAGE_GETFILENAME = "";

	public final static String[] HELP_GETFILENAME = new String[] { "Get file name", };

	public int cmdGetfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current Filename : "
				+ ((UserAdminManagerConfig) _config).getFileName());
		return 0;
	}

	public final static String USAGE_READFILE = "";

	public final static String[] HELP_READFILE = new String[] { "Read the file", };

	public int cmdReadfile(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Object[] arguments = { System.getProperty("org.avm.home") };
		String filename = MessageFormat.format(
				((UserAdminManagerConfig) _config).getFileName(), arguments);
		BufferedReader fic;
		int nb = 0;
		out.println("Reading current Filename : " + filename);
		try {
			fic = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "ISO8859-1"));
			String line = fic.readLine();
			while (line != null) {
				if (line.trim().length() != 0){
					nb++;
					out.println(nb+"-> " + line);
				}

				line = fic.readLine();
			}
		} catch (IOException e) {
			out.println("IOException:"+e.getMessage());
			e.printStackTrace(out);
		}
		out.flush();
		out.println();
		out.println(nb + " lines read");

		return 0;
	}

	public final static String USAGE_REINITMEMBERS = "";

	public final static String[] HELP_REINITMEMBERS = new String[] { "Re-init members ...", };

	public int cmdReinitmembers(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			_peer.reinitMembers();
			out.println("ok");
		} catch (Throwable e) {
			out.println("Erreur : " + e.getMessage());
			e.printStackTrace(out);
		}
		out.flush();
		// cmdList(opts, in, out, session);
		return 0;
	}

	// Add group
	public final static String USAGE_ADDGROUP = "<group>";

	public final static String[] HELP_ADDGROUP = new String[] { "Ajout de groupe" };

	public int cmdAddgroup(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String group = ((String) opts.get("group")).trim();

		_peer.createGroup(group);
		return 0;
	}

	// Add member to group
	public final static String USAGE_ADDMEMBER = "<role> <group>";

	public final static String[] HELP_ADDMEMBER = new String[] { "Associe un groupe a un membre" };

	public int cmdAddmember(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String role = ((String) opts.get("role")).trim();
		String group = ((String) opts.get("group")).trim();

		try {
			_peer.addMember(role, group);
		} catch (NoSuchFieldException e) {
			out.println("Erreur : " + e.getMessage());
		}
		return 0;
	}

	/*
	 * // Add required member to group public final static String
	 * USAGE_ADDREQUIREDMEMBER = "<role> <group>";
	 * 
	 * public final static String[] HELP_ADDREQUIREDMEMBER = new String[] {
	 * "Associe un groupe 'requis' a un membre" };
	 * 
	 * public int cmdAddrequiredmember(Dictionary opts, Reader in, PrintWriter
	 * out, Session session) { String role = ((String) opts.get("role")).trim();
	 * String group = ((String) opts.get("group")).trim();
	 * 
	 * try { _peer.addRequiredMember(role, group); } catch (NoSuchFieldException
	 * e) { out.println("Erreur : " + e.getMessage()); } return 0; }
	 */
	// Remove member to group
	public final static String USAGE_DELMEMBER = "<role> <group>";

	public final static String[] HELP_DELMEMBER = new String[] { "Dissocie un membre d'un groupe" };

	public int cmdDelmember(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String role = ((String) opts.get("role")).trim();
		String group = ((String) opts.get("group")).trim();

		_peer.removeMember(role, group);
		return 0;
	}

	// Add user
	public final static String USAGE_ADDUSER = "-u #user# -p #properties# -c #credentials# -g #groups";

	public final static String[] HELP_ADDUSER = new String[] {
			"Ajout d'utilisateurs", "add -u 007 -p " };

	public int cmdAdduser(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String user = ((String) opts.get("-u")).trim();
		String sProperties = ((String) opts.get("-p")).trim();
		String sCredentials = ((String) opts.get("-c")).trim();
		String sGroups = ((String) opts.get("-g")).trim();
		Properties properties = parse(sProperties);
		Properties credentials = parse(sCredentials);

		_peer.createUser(user, properties, credentials);

		StringTokenizer t = new StringTokenizer(sGroups, ",");
		while (t.hasMoreElements()) {
			String group = (String) t.nextElement();
			try {
				_peer.addMember(user, group);
			} catch (NoSuchFieldException e) {
				out.println("Erreur : " + e.getMessage());
			}
		}
		return 0;
	}

	private Properties parse(String str) {
		StringTokenizer t = new StringTokenizer(str, " ");
		Properties p = new Properties();
		while (t.hasMoreElements()) {
			String element = (String) t.nextElement();
			int idx = element.indexOf("=");
			String key = element.substring(0, idx);
			String value = element.substring(idx + 1);
			p.put(key, value);
		}
		return p;
	}

	// delete
	public final static String USAGE_REMOVEALL = "<confirm>";

	public final static String[] HELP_REMOVEALL = new String[] { "Suppression de tous les utilisateurs" };

	public int cmdRemoveall(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String confirm = ((String) opts.get("confirm"));

		if (confirm.equals("confirm")) {
			try {
				_peer.removeAllUsers();
				out.println("All users removed.");
			} catch (Throwable e) {
				out.println("Error : " + e.getMessage());
				e.printStackTrace(out);
			}
			out.flush();
		} else {
			out.println("Must use : 'removeall confirm' !");
		}

		return 0;
	}

	// delete
	public final static String USAGE_DELUSER = "<idu>";

	public final static String[] HELP_DELUSER = new String[] { "Suppression d'utilisateurs" };

	public int cmdDeluser(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String idu = ((String) opts.get("idu")).trim();

		_peer.removeUser(idu);

		return 0;
	}

	// Count
	public final static String USAGE_COUNT = "";

	public final static String[] HELP_COUNT = new String[] { "Get number of users.", };

	public int cmdCount(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Role[] roles = _peer.getRoles();
		int count = 0;
		for (int i = 0; i < roles.length; i++) {
			Role role = roles[i];
			if (role instanceof Group) {
				// ignore
			} else if (role instanceof User) {
				count++;
			}
		}
		out.println("Number of users : " + count++);
		return 0;
	}

	// Get user
	public final static String USAGE_GETUSER = "<tag>";

	public final static String[] HELP_GETUSER = new String[] { "Return user and user's properties", };

	public int cmdGetuser(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String tag = ((String) opts.get("tag")).toLowerCase();
		Role[] roles = _peer.getRoles();
		boolean found = false;
		for (int i = 0; i < roles.length; i++) {
			Role role = roles[i];
			if (role instanceof Group) {
				// ignore
			} else if (role instanceof User) {
				User user = (User) role;
				String matricule = user.getName().toLowerCase();
				String nom = (String) user.getProperties().get("nom");
				if ((matricule.equals(tag))
						|| (nom != null && nom.toLowerCase().indexOf(tag) != -1)) {
					out.println("User : " + user.getName() + " "
							+ user.getProperties() + "("
							+ user.getCredentials() + ")");
					found = true;
				}
			}
		}
		if (!found) {
			out.println("No user match : " + tag);
		}
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List roles.", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Role[] roles = _peer.getRoles();
		for (int i = 0; i < roles.length; i++) {
			Role role = roles[i];
			if (role instanceof Group) {
				Group group = (Group) role;
				Role[] rs;
				rs = group.getMembers();
				StringBuffer members = new StringBuffer();
				for (int j = 0; rs != null && j < rs.length; j++) {
					members.append(rs[j].getName() + ", ");
				}
				rs = group.getRequiredMembers();
				StringBuffer requiredMembers = new StringBuffer();
				for (int j = 0; rs != null && j < rs.length; j++) {
					requiredMembers.append(rs[j].getName() + ", ");
				}
				out.println("Group : " + group.getName() + " members={"
						+ members + "}  required members={" + requiredMembers
						+ "}");
			} else if (role instanceof User) {
				User user = (User) role;
				out.println("User : " + user.getName() + " properties="
						+ user.getProperties() + ", secrets="
						+ user.getCredentials());
			} else {
				out.println("Role : " + role.getName());
			}
		}
		return 0;
	}

	// Add matricule
	public final static String USAGE_ADDMATRICULE = "-m #matricule# -p #prenom# -n #nom# [-c #codesecret#]  [-g #groups]";

	public final static String[] HELP_ADDMATRICULE = new String[] {
			"Ajout d'utilisateurs", "add -u 007 -p " };

	public int cmdAddmatricule(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String matricule = ((String) opts.get("-m")).trim();
		String nom = ((String) opts.get("-n"));
		String prenom = ((String) opts.get("-p"));
		String codesecret = ((String) opts.get("-c"));
		String sGroups = ((String) opts.get("-g"));
		if (sGroups == null) {
			sGroups = "conducteur";
		}

		Properties properties = new Properties();
		properties.put("matricule", matricule);
		properties.put("nom", (nom != null) ? nom : "");
		properties.put("prenom", (prenom != null) ? prenom : "");
		Properties credentials = new Properties();
		credentials.put("codesecret", (codesecret != null) ? codesecret : "");

		_peer.createUser(matricule, properties, credentials);

		StringTokenizer t = new StringTokenizer(sGroups, ",");
		while (t.hasMoreElements()) {
			String group = (String) t.nextElement();
			try {
				_peer.addMember(matricule, group);
			} catch (NoSuchFieldException e) {
				out.println("Erreur : " + e.getMessage());
			}
		}
		return 0;
	}

}
