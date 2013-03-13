package org.avm.elementary.management.addons.bundle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.StringTokenizer;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementImpl;
import org.avm.elementary.management.addons.ManagementService;
import org.avm.elementary.management.core.Terminal;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "management";

	private ManagementService _peer;

	public CommandGroupImpl(ComponentContext context, ManagementService peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the management.");
		_peer = peer;
	}

	// Execute management commands
	public final static String USAGE_INIT = "<confirm>";

	public final static String[] HELP_INIT = new String[] { "Init (uninstall) all bundles except 'system bundle' and 'management bundle'\n"
			+ "\tusage : init ALL" };

	public int cmdInit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String confirm = ((String) opts.get("confirm")).trim();

		if (confirm != null && confirm.equals("ALL")) {
			try {
				((ManagementImpl) _peer).execute("init", "", out);
			} catch (CommandException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}

		} else {
			out.println("please use 'init ALL'");
		}
		return 0;
	}

	// -- STOP
	public final static String USAGE_STOP = "[<bundles>] ...";

	public final static String[] HELP_STOP = new String[] { "Stop a bundle" };

	public int cmdStop(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "stop";

		String[] bundles = (String[]) opts.get("bundles");
		if (bundles != null) {
			StringBuffer parameters = new StringBuffer();
			parameters.append("bundle=");
			for (int i = 0; i < bundles.length; i++) {
				String b = bundles[i];

				parameters.append(b + " ");
			}

			try {
				((ManagementImpl) _peer).execute(cmd, parameters.toString(),
						out);
			} catch (CommandException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}
		}

		return 0;
	}

	// -- START
	public final static String USAGE_START = "[<bundles>] ...";

	public final static String[] HELP_START = new String[] { "Start a bundle" };

	public int cmdStart(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "start";

		String[] bundles = (String[]) opts.get("bundles");
		if (bundles != null) {
			StringBuffer parameters = new StringBuffer();
			parameters.append("bundle=");
			for (int i = 0; i < bundles.length; i++) {
				String b = bundles[i];

				parameters.append(b + " ");
			}

			try {
				((ManagementImpl) _peer).execute(cmd, parameters.toString(),
						out);
			} catch (CommandException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}
		}

		return 0;
	}

	// -- UPDATE
	public final static String USAGE_UPDATE = "[<bundles>] ...";

	public final static String[] HELP_UPDATE = new String[] { "Update a bundle" };

	public int cmdUpdate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "update";
		String[] bundles = (String[]) opts.get("bundles");
		if (bundles != null) {
			StringBuffer parameters = new StringBuffer();
			parameters.append("bundle=");
			for (int i = 0; i < bundles.length; i++) {
				String b = bundles[i];

				parameters.append(b + " ");
			}

			try {
				((ManagementImpl) _peer).execute(cmd, parameters.toString(),
						out);
			} catch (CommandException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}
		}

		return 0;
	}

	// -- SETDOWNLOADURL
	public final static String USAGE_SETDOWNLOADURL = "[<url>]";

	public final static String[] HELP_SETDOWNLOADURL = new String[] {
			"Set download url", "'setdownloadurl default' to reset to default" };

	public int cmdSetdownloadurl(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String url = ((String) opts.get("url"));
		if (url != null) {
			try {
				if (url.equals("default")) {
					_peer.setDownloadURL(null);
				} else {
					_peer.setDownloadURL(new URL(url));
				}
			} catch (MalformedURLException e) {
				out.println("Error :" + e.getMessage());
			}
		}
		out.println(_peer.getDownloadURL());
		return 0;
	}

	// -- SETUPLOADURL
	public final static String USAGE_SETUPLOADURL = "[<url>]";

	public final static String[] HELP_SETUPLOADURL = new String[] {
			"Set upload url", "'setuploadurl default' to reset to default" };

	public int cmdSetuploadurl(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String url = ((String) opts.get("url"));
		if (url != null) {
			try {
				if (url.equals("default")) {
					_peer.setUploadURL(null);
				} else {
					_peer.setUploadURL(new URL(url));
				}

			} catch (MalformedURLException e) {
				out.println("Error :" + e.getMessage());
			}
		}
		out.println(_peer.getUploadURL());
		return 0;
	}

	// -- AUTOUPDATE
	public final static String USAGE_AUTOUPDATE = "";

	public final static String[] HELP_AUTOUPDATE = new String[] { "Autoupdate all bundles" };

	public int cmdAutoupdate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			((ManagementImpl) _peer).synchronize(out);
		} catch (Exception e) {
			out.println(e);
		}
		return 0;
	}

	// -- DIFFBUNDLES
	public final static String USAGE_DIFFBUNDLES = "";

	public final static String[] HELP_DIFFBUNDLES = new String[] { "Autoupdate all bundles" };

	public int cmdDiffbundles(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "getbundlelist";
		String parameters = "";

		try {
			((ManagementImpl) _peer).execute(cmd, parameters, out);

		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}

		return 0;
	}

	// -- SETPROP
	public final static String USAGE_SETPROP = "<param>";

	public final static String[] HELP_SETPROP = new String[] { "Set system property" };

	public int cmdSetprop(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "setprop";
		String parameters = ((String) opts.get("param")).trim();

		try {
			((ManagementImpl) _peer).execute(cmd, parameters, out);

		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}

		return 0;
	}

	// -- GETPROP
	public final static String USAGE_GETPROP = "[<key>]";

	public final static String[] HELP_GETPROP = new String[] { "Get system property" };

	public int cmdGetprop(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "getprop";
		String keys = ((String) opts.get("key"));
		StringBuffer parameters = new StringBuffer();
		parameters.append((keys == null) ? "" : "keys=" + keys + ";");
		parameters.append("report=false");
		try {
			((ManagementImpl) _peer).execute(cmd, parameters.toString(), out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}

		return 0;
	}

	// -- STATUS
	public final static String USAGE_STATUS = "[-c #SA#] [-d #DE#] [<filter>] ";

	public final static String[] HELP_STATUS = new String[] { "List bundles status" };

	public int cmdStatus(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "status";
		String filters = ((String) opts.get("filter"));
		String colored = ((String) opts.get("-c"));
		String details = ((String) opts.get("-d"));
		StringBuffer parameters = new StringBuffer();
		parameters.append((filters == null) ? "" : "filter=" + filters + ";");
		parameters.append((details == null) ? "" : "complete="
				+ details.equalsIgnoreCase("true") + ";");
		parameters.append("colored="
				+ ((colored == null || !colored.equals("true")) ? "false"
						: "true") + ";");
		parameters.append("report=false");

		try {
			((ManagementImpl) _peer).execute(cmd, parameters.toString(), out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}

		return 0;
	}

	// -- RESTART
	public final static String USAGE_RESTART = "[<bundles>] ...";

	public final static String[] HELP_RESTART = new String[] { "Restart bundle" };

	public int cmdRestart(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "restart";

		String[] bundles = (String[]) opts.get("bundles");
		if (bundles != null) {
			StringBuffer parameters = new StringBuffer();
			parameters.append("bundle=");
			for (int i = 0; i < bundles.length; i++) {
				String b = bundles[i];

				parameters.append(b + " ");
			}

			try {
				((ManagementImpl) _peer).execute(cmd, parameters.toString(),
						out);
			} catch (CommandException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}
		}

		return 0;
	}

	// -- SHUTDOWN
	public final static String FLAG_SHUTDOWN = "-t";

	public final static String FLAG_REBOOT = "-r";

	public final static String USAGE_SHUTDOWN = "[-t #T#] [-r #R#]";

	public final static String[] HELP_SHUTDOWN = new String[] { "Shutdown framework "
			+ "-t <time in sec before shutdown>"
			+ "-r <fwk|debug|sys> reboot framework or system" };

	public int cmdShutdown(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "shutdown";
		String shutdown = ((String) opts.get(FLAG_SHUTDOWN));
		String reboot = ((String) opts.get(FLAG_REBOOT));
		StringBuffer parameters = new StringBuffer();
		parameters.append((shutdown == null) ? "" : "timeout=" + shutdown);
		parameters.append(";");
		parameters.append((reboot == null) ? "" : "reboot=" + reboot);
		try {
			((ManagementImpl) _peer).execute(cmd, parameters.toString(), out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}

		return 0;
	}

	// -- UNINSTALL
	public final static String USAGE_UNINSTALL = "[<bundles>] ...";

	public final static String[] HELP_UNINSTALL = new String[] { "Uninstall bundler" };

	public int cmdUninstall(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "uninstall";

		String[] bundles = (String[]) opts.get("bundles");
		if (bundles != null) {
			StringBuffer parameters = new StringBuffer();
			parameters.append("bundle=");
			for (int i = 0; i < bundles.length; i++) {
				String b = bundles[i];

				parameters.append(b + " ");
			}

			try {
				((ManagementImpl) _peer).execute(cmd, parameters.toString(),
						out);
			} catch (CommandException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}
		}

		return 0;
	}

	// -- bundle information
	public final static String USAGE_BUNDLE = "<bundle>";

	public final static String[] HELP_BUNDLE = new String[] { "show bundle informations" };

	public int cmdBundle(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "bundle";
		String parameters = ((String) opts.get("bundle"));
		parameters = (parameters == null) ? "" : "bundle=" + parameters;
		try {
			((ManagementImpl) _peer).execute(cmd, parameters, out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	// -- set bundle startlevel
	public final static String USAGE_SETBSL = "<bundle> [<startlevel>]";

	public final static String[] HELP_SETBSL = new String[] { "set bundle start level" };

	public int cmdSetbsl(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "setbsl";
		String sl = ((String) opts.get("startlevel"));
		String parameters = "bundle=" + ((String) opts.get("bundle"));
		parameters = (sl == null) ? parameters : parameters + ";startlevel="
				+ sl;

		try {
			((ManagementImpl) _peer).execute(cmd, parameters, out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	// -- set framework startlevel
	public final static String USAGE_SETFWSL = "[<startlevel>]";

	public final static String[] HELP_SETFWSL = new String[] { "set framework start level" };

	public int cmdSetfwsl(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "setfwsl";
		String parameters = (String) opts.get("startlevel");
		parameters = (parameters == null) ? "" : "startlevel="
				+ parameters.trim();
		try {
			((ManagementImpl) _peer).execute(cmd, parameters, out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	// -- system informations
	public final static String USAGE_SYSINFO = "";

	public final static String[] HELP_SYSINFO = new String[] { "Get system info" };

	public int cmdSysinfo(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "systeminfo";
		try {
			((ManagementImpl) _peer).execute(cmd, "", out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	// -- headers bundle information
	public final static String USAGE_HEADERS = "<bundle>";

	public final static String[] HELP_HEADERS = new String[] { "Get headers bundle information" };

	public int cmdHeaders(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "headers";
		String parameters = "bundle=" + ((String) opts.get("bundle"));
		try {
			((ManagementImpl) _peer).execute(cmd, parameters, out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	// -- execute script from url
	public final static String USAGE_SCRIPT = "<url>";

	public final static String[] HELP_SCRIPT = new String[] { "Get headers bundle information" };

	public int cmdScript(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		try {
			String surl = ((String) opts.get("url"));
			URL url = new URL(surl);
			((ManagementImpl) _peer).runScript(url);
			out.println("done");
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	// -- REFRESH
	public final static String USAGE_REFRESH = "[<bundles>] ...";

	public final static String[] HELP_REFRESH = new String[] { "Update a bundle" };

	public int cmdRefresh(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "refresh";

		StringBuffer parameters = new StringBuffer();
		String[] bundles = (String[]) opts.get("bundles");
		if (bundles != null) {
			parameters.append("bundle=");
			for (int i = 0; i < bundles.length; i++) {
				String b = bundles[i];

				parameters.append(b + " ");
			}
		}

		try {
			((ManagementImpl) _peer).execute(cmd, parameters.toString(), out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}

		return 0;
	}

	// -- headers bundle information
	public final static String USAGE_SYSLOG = "<start/stop> [<bindAddress> <hostAdress>]";

	public final static String[] HELP_SYSLOG = new String[] { "start/stop syslog" };

	public int cmdSyslog(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = ((String) opts.get("start/stop"));
		try {
			if (cmd.equalsIgnoreCase("start")) {
				String bindAddress = ((String) opts.get("bindAddress"));
				String hostAdress = ((String) opts.get("hostAdress"));
				if (bindAddress != null && hostAdress != null) {
					((ManagementImpl) _peer).enableSyslog(bindAddress,
							hostAdress);
					out.println("syslog started.");
				} else {
					out.println("Usage : syslog stop | start <bindAddress> <hostAdress>");
				}
			} else if (cmd.equalsIgnoreCase("stop")) {
				((ManagementImpl) _peer).disableSyslog();
				out.println("syslog stopped.");
			} else {
				out.println("unknown command '" + cmd + "'");
				out.println("Usage : syslog stop | start <bindAddress> <hostAdress>");
			}
		} catch (Exception e) {
			out.println("Error: " + e.getMessage());
		}
		return 0;
	}

	// -- execute script from url
	public final static String USAGE_UPLOAD = "[-f #F#] [-r #R#] [<file>] ";

	public final static String[] HELP_UPLOAD = new String[] {
			"Upload files (use /data/upload,if <file> is empty)",
			"-f <true=default|false> : force copy even if file created today ",
			"-r <true=default|false> : remove file after copy " };

	public int cmdUpload(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "upload";
		String forceCopy = ((String) opts.get("-f"));
		if (forceCopy == null || !forceCopy.equalsIgnoreCase("false")) {
			forceCopy = "true";
		}
		String remove = ((String) opts.get("-r"));
		if (remove == null || !remove.equalsIgnoreCase("false")) {
			remove = "true";
		}
		String filepath = ((String) opts.get("file"));
		if (filepath == null) {
			filepath = System.getProperty("org.avm.home") + "/data/upload";
		}
		StringBuffer parameters = new StringBuffer();
		parameters.append("filepath=" + filepath);
		parameters.append(";remove=" + remove);
		parameters.append(";force=" + forceCopy);

		try {
			((ManagementImpl) _peer).execute(cmd, parameters.toString(), out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	// -- execute script from url
	public final static String USAGE_CONFIGURATION = "<action> [<property>] [<filename>]";

	public final static String[] HELP_CONFIGURATION = new String[] {
			"Load/Save configuration file (avm.properties)", "load", "save",
			"save org.sample.fee" };

	public int cmdConfiguration(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "configuration";
		String parameter = "action=" + ((String) opts.get("action"));
		String property = ((String) opts.get("property"));
		String filename = ((String) opts.get("filename"));
		if (property != null) {
			parameter += ";property=" + property;
		}
		if (filename != null) {
			parameter += ";filename=" + filename;
		}
		try {
			((ManagementImpl) _peer).execute(cmd, parameter, out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	// -- Shell
	public final static String USAGE_SHELL = "[<cmd>] ...";

	public final static String[] HELP_SHELL = new String[] {
			"Execute shell command", "ls", "rm", "cd", "pwd", "touch <file>",
			"mkdir <dir>", "cat <file>", "zcat <file>",
			"wget <remote> <local>", "exec <proc>", "kill <pid>",
			"nmap <host> <port>", "ip <gprs|wifi>" };

	public int cmdShell(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "shell";

		String[] params = (String[]) opts.get("cmd");
		if (params != null) {
			StringBuffer bufparameters = new StringBuffer();
			bufparameters.append("cmd=");
			for (int i = 0; i < params.length; i++) {
				String p = params[i];

				bufparameters.append(p);
				bufparameters.append(" ");
			}

			try {
				((ManagementImpl) _peer).execute(cmd, bufparameters.toString(),
						out);
			} catch (CommandException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}
		}

		return 0;
	}

	public final static String USAGE_DEPENDENCY = "[<bundle>]";

	public final static String[] HELP_DEPENDENCY = new String[] { "show bundle dependency informations" };

	public int cmdDependency(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "dependency";
		String parameters = ((String) opts.get("bundle"));
		parameters = (parameters == null) ? "" : "bundle=" + parameters;
		try {
			((ManagementImpl) _peer).execute(cmd, parameters, out);
		} catch (CommandException e) {
			out.println(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return 0;
	}

	public final static String USAGE_REPORT = "<destination> [<cmd>] ...";

	public final static String[] HELP_REPORT = new String[] { "send result of cmd to destination" };

	public int cmdReport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = "report";

		String[] cmds = (String[]) opts.get("cmd");
		StringBuffer parameters = new StringBuffer();
		if (cmds != null) {
			parameters.append("command=");
			for (int i = 0; i < cmds.length; i++) {
				String p = cmds[i];

				parameters.append(p);
				parameters.append(" ");
			}
			String destination = ((String) opts.get("destination"));
			parameters.append(";destination=");
			parameters.append(destination);

			try {
				((ManagementImpl) _peer).execute(cmd, parameters.toString(),
						out);
			} catch (CommandException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}
		} else {
			out.println("command can not be null!");
		}
		return 0;
	}

	public final static String USAGE_ID = "[-n #NAME#] [-o #OWNER#]";

	public final static String[] HELP_ID = new String[] { "Set/Get Terminal identity" };

	public int cmdId(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String name = (String) opts.get("-n");
		String owner = (String) opts.get("-o");

		if (name == null && owner == null) {
			out.println("name=" + Terminal.getInstance().getName());
			out.println("owner=" + Terminal.getInstance().getOwner());
			out.println("exp.name="
					+ System.getProperty("org.avm.exploitation.name"));
		}
		else{
			
		}

		return 0;
	}

	public final static String USAGE_HOST = "[-f #F#] <host> [<ipaddress>]";

	public final static String[] HELP_HOST = new String[] { "add/remove address" };

	public int cmdHost(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String hostsFile = ((String) opts.get("-f"));
		if (hostsFile == null) {
			hostsFile = "/etc/hosts";
		}

		String host = ((String) opts.get("host"));
		String ipaddress = ((String) opts.get("ipaddress"));
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(hostsFile));
			StringBuffer buffer = host(reader, host, ipaddress, out);
			if (buffer != null) {
				try {
					FileOutputStream of = new FileOutputStream(hostsFile, false);
					of.write(buffer.toString().getBytes());
					of.flush();
					of.close();
				} catch (IOException e) {
					out.println("Error :" + e.getMessage());
					e.printStackTrace();
				}
			}

		} catch (FileNotFoundException e) {
			out.println("Error :" + e.getMessage());
			e.printStackTrace();
		}

		return 0;
	}

	public StringBuffer host(BufferedReader reader, String host,
			String ipaddress, PrintWriter out) {
		StringBuffer buffer = new StringBuffer();
		String line = null;
		boolean added = false;
		try {

			line = reader.readLine();
			while (line != null) {
				line = line.trim();
				if (line.startsWith("#")) {
					buffer.append(line);
					buffer.append("\n");
				} else if ((ipaddress == null || line.indexOf(ipaddress) != -1)
						|| (line.indexOf(host) != -1)) {
					line = line.replace(' ', '\t');
					StringTokenizer t = new StringTokenizer(line, "\t");
					if (t.hasMoreElements()) {

						String addr = t.nextToken();
						String name = null;
						StringBuffer newLineBuf = new StringBuffer();
						newLineBuf.append(addr);
						newLineBuf.append('\t');

						// -- reconstruction de la ligne sans 'host'
						boolean hostAlreadyExist = false;
						while (t.hasMoreElements()) {
							name = t.nextToken();
							if (!name.trim().equals(host)) {
								newLineBuf.append(name);
								if (t.hasMoreElements()) {
									newLineBuf.append('\t');
								}
							} else {
								hostAlreadyExist = true;
							}
						}

						// -- retourne adresse actuelle de 'host'
						if (hostAlreadyExist && ipaddress == null) {
							//out.println(addr);
							return new StringBuffer(addr);
						}

						// -- ajout de l'hote si necessaire
						if (hostAlreadyExist == false
								&& (ipaddress != null && !ipaddress
										.equals("del"))) {
							newLineBuf.append('\t');
							newLineBuf.append(host);
							added = true;
						}

						// -- ajout de la ligne dans le fichier si au moins un
						// host
						if (!newLineBuf.toString().trim().equals(addr)) {
							buffer.append(newLineBuf);
							buffer.append("\n");
							if (buffer.toString().indexOf(host) != -1
									|| (ipaddress != null && ipaddress
											.equals("del"))) {
								added = true;
							}
						}
					}

				} else {
					buffer.append(line);
					buffer.append("\n");
				}

				line = reader.readLine();
			}
		} catch (Exception e) {
			out.println("Error (last line: " + line + ")");
			out.println("Error :" + e.getMessage());
			e.printStackTrace();
		}

		if (!added && ipaddress != null && !ipaddress.equals("del")) {
			// aucune ligne trouvee avec ipadress ou host : donc ajout
			boolean ok = false;
			try {
				InetAddress ad = InetAddress.getByName(ipaddress);
				ok = (ad.getHostAddress().replace('/', ' ').trim())
						.equals(ipaddress);
			} catch (Throwable e) {
				ok = false;
			}
			if (ok) {
				buffer.append(ipaddress);
				buffer.append('\t');
				buffer.append(host);
				buffer.append("\n");
			} else {
				out.println("not a valid ip address!");
				return null;
			}
		}
		// -- adresse de 'host' (inconnu)
		if (ipaddress == null) {
			out.println("unknown");
			return null;
		}
		return buffer;
	}

}
