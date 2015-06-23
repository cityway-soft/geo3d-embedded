/**
 * 
 */
package org.avm.elementary.management.addons;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.management.addons.command.CommandFactory;
import org.avm.elementary.management.addons.usb.USBManagementImpl;
import org.avm.elementary.management.addons.wifi.WifiManagementImpl;
import org.avm.elementary.management.core.Management;
import org.avm.elementary.management.core.utils.DataUploadClient;
import org.avm.elementary.management.core.utils.Utils;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.messenger.MessengerInjector;
import org.json.JSONException;
import org.json.JSONObject;
import org.knopflerfish.service.console.ConsoleService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.util.measurement.State;

public class ManagementImpl implements ManageableService, ManagementService,
		ConsumerService, MessengerInjector {

	private Logger _log;

	private ConsoleService _cs;

	private WifiManagementImpl _wifiManagement;

	private org.avm.elementary.log4j.Logger _syslog;

	private BundleContext _context;

	private USBManagementImpl _usbManagement;

	private Scheduler _scheduler;

	private boolean _disableUpload = false;

	private Messenger _messenger;

	private ServiceReference managementServiceReference;

	private static final String SMS_PROTOCOL_TAG = "sms://";
	private static final String FTP_PROTOCOL_TAG = "ftp://";
	private static final String CTW_PROTOCOL_TAG = "ctw";

	public ManagementImpl() {
		_log = Logger.getInstance(ManagementService.class);
		// _log.setPriority(Priority.DEBUG);
		_scheduler = new Scheduler();
	}

	public void start() {
		checkManagementBundlePackages(_context, new PrintWriter(System.out));
		_scheduler.schedule(new Runnable() {
			private int MAX_TRY = 5;

			public void run() {
				int count = 1;
				boolean uploadDone = false;
				while (count < MAX_TRY && uploadDone == false
						&& !_disableUpload) {
					try {
						uploadDone = synchronizeData();
					} catch (Exception e) {
						_log.error(e);

					}
					if (!uploadDone) {
						_log.debug("Upload data failed : " + count + "/"
								+ MAX_TRY);
						count++;
						try {
							Thread.sleep(5000);
						} catch (Throwable t) {
						}
					}

				}
				if (uploadDone) {
					_log.debug("Upload data done.");
				} else {
					_log.debug("Upload data failed after #" + MAX_TRY);
				}
			}

		}, 10000);
	}

	public void stop() {

	}

	/**
	 * @param context
	 * 
	 */
	public void setBundleContext(BundleContext context) {
		_context = context;
	}

	protected void checkManagementBundlePackages(BundleContext context,
			PrintWriter out) {
		org.osgi.framework.ServiceReference packageAdminRef = context
				.getServiceReference("org.osgi.service.packageadmin.PackageAdmin"); //$NON-NLS-1$
		boolean result = true;
		long t0 = System.currentTimeMillis();
		Bundle bundle;

		bundle = context.getBundle(1);
		if (bundle == null
				|| !bundle.getSymbolicName().equals(
						"org.avm.elementary.management.core")) {
			// -- management core n'est pas le bundleid 1
			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				bundle = bundles[i];
				if (bundle.getSymbolicName().equals(
						"org.avm.elementary.management.core")) {
					break;
				}
			}
		}
		out.println("Find bundle management : " + bundle + " ("
				+ bundle.getSymbolicName() + ")");
		if (packageAdminRef != null) {
			org.osgi.service.packageadmin.PackageAdmin packageAdmin = (org.osgi.service.packageadmin.PackageAdmin) context
					.getService(packageAdminRef);
			if (packageAdmin != null) {
				try {
					org.osgi.service.packageadmin.ExportedPackage exportedpkgs[] = packageAdmin
							.getExportedPackages(bundle);
					if (exportedpkgs != null) {
						for (int i = 0; i < exportedpkgs.length; i++) {
							org.osgi.service.packageadmin.ExportedPackage exportedpkg = exportedpkgs[i];
							exportedpkg.getImportingBundles(); // Exception
							// lorsque pb
							// package !!!
						}
					}
				} catch (Throwable t) {
					result = false;
				}
			}
		}

		if (result == false) {
			out.println("!!!!!!!!!!!!!!!!!!!!    WARNING    !!!!!!!!!!!!!!!!!!!!!!!!");
			out.println("!       Management Core Packages failure detected         !");
			out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Bundle[] bundles = new Bundle[] { bundle };
			getPackageAdminService().refreshPackages(bundles);
			getPackageAdminService().resolveBundles(bundles);
		}
		out.println("Time to check management core : "
				+ (System.currentTimeMillis() - t0) + "ms.");

	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			_log.debug("Receive state : '" + state + "'");
			String stateName = state.getName().toLowerCase();
			if (stateName.indexOf("wifi") != -1) {
				if (_wifiManagement == null) {
					_wifiManagement = new WifiManagementImpl(this);
					_wifiManagement.setSyslog(_syslog);
				}
				_disableUpload = true;
				_wifiManagement.notify(o);
			} else if (stateName.indexOf("usb") != -1) {
				if (_usbManagement == null) {
					_usbManagement = new USBManagementImpl(this);
				}
				_disableUpload = true;
				_usbManagement.notify(o);
			}
		} else if (o instanceof org.avm.business.protocol.management.Management) {
			org.avm.business.protocol.management.Management cmd = (org.avm.business.protocol.management.Management) o;
			String commands = cmd.getText().trim();
			_log.debug("Receive management : '" + commands + "'");
			String cmds = URLDecoder.decode(commands);
			_log.debug("Receive management urldecoded : '" + cmds + "'");
			StringTokenizer t = new StringTokenizer(cmds, "\n");
			while (t.hasMoreTokens()) {
				String s = t.nextToken();
				_log.info("Run management command : '" + s + "'");
				executeScript(s);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.avm.elementary.management.core.ManagementService#execute(java.lang
	 * .String, java.util.Properties, java.io.PrintWriter)
	 */
	public void execute(String commandName, Properties parameters,
			PrintWriter out) throws CommandException, IOException {
		Command cmd;
		try {
			cmd = CommandFactory.create(commandName);
			if (cmd != null) {
				cmd.execute(_context, parameters, out, this);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param parameters
	 * @return
	 * @throws ParseException
	 */
	protected Properties getParametersWithValues(String parameters)
			throws ParseException {
		Properties p = new Properties();
		StringTokenizer t = new StringTokenizer(parameters, ";"); //$NON-NLS-1$
		while (t.hasMoreElements()) {
			String token = t.nextToken();
			StringTokenizer t2 = new StringTokenizer(token, "="); //$NON-NLS-1$
			String key = t2.nextToken();
			if (t2.hasMoreElements() == false) {
				throw new ParseException("ERR_SYNTAX : " + token, 0); //$NON-NLS-1$
			}
			String value = t2.nextToken();

			p.put(key, value);
		}
		return p;
	}

	public void execute(String commandName, String params, PrintWriter out)
			throws CommandException, IOException {
		Properties p;
		try {
			p = getParametersWithValues(params);
			execute(commandName, p, out);
		} catch (ParseException e) {
			out.println("ERROR: " + e.getMessage());
		}
	}

	public void runScript(URL url) {
		_log.info("Running script... :" + url); //$NON-NLS-1$

		if (_cs == null) {
			_log.error("Console Service not set !"); //$NON-NLS-1$
		} else {
			InputStream is;
			try {
				is = url.openStream();
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				String cmd = r.readLine();
				_log.info("-Running in console : " + cmd);
				String result;
				while (cmd != null) {
					_log.info("Running in console : " + cmd);
					result = _cs.runCommand(cmd);
					_log.info("Result:" + result);
					cmd = r.readLine();
				}
			} catch (IOException e) {
				_log.error("IOException " + e.getMessage()); //$NON-NLS-1$
			}
		}
	}

	public void executeScript(String cmd) {
		if (_cs == null) {
			_log.error("Console Service not set !"); //$NON-NLS-1$
		} else {
			String destination = null;
			String id = null;
			cmd = cmd.trim();
			if (cmd.startsWith("@")) {
				int idx = cmd.indexOf(" ");
				String tmp = cmd.substring(1, idx);
				idx = tmp.indexOf("!");
				destination = tmp.substring(0, idx);
				id = tmp.substring(idx + 1);
				cmd = cmd.substring(idx + 2 + id.length()).trim();
				System.err.println("id=" + id + ", dest=" + destination
						+ ", cmd=" + cmd);
			}

			String result = _cs.runCommand(cmd);

			if (result != null && result.trim().length() != 0) {
				result=result.trim();
				if (result.startsWith("{") && result.endsWith("}")) {
					try {
						JSONObject jsonResult = new JSONObject(result);
						JSONObject response = new JSONObject();
						response.put("id", id);
						response.put("response", jsonResult);
						result = response.toString();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (destination != null) {
					int idx;
					if (destination.startsWith(SMS_PROTOCOL_TAG)) {
						idx = destination.indexOf(SMS_PROTOCOL_TAG);
						String number = destination.substring(idx
								+ SMS_PROTOCOL_TAG.length());
						result = result.substring(0,
								Math.min(result.length(), 159));
						result = result.replace('\n', ',');
						result = result.replace('\r', ' ');
						String command = "/media.sms sendmsg  -d " + number
								+ " -t texto -m \"" + result + "\"";
						_log.info("Execute cmd :" + command);
						result = runScript(command);
						_log.info("result :" + result);
					} else if (destination.startsWith(FTP_PROTOCOL_TAG)) {
						String path = Utils.formatURL(destination, false);
						String remotefile = Utils.formatURL("$i_response.txt",
								false);
						_log.info("ftp path :" + path);
						try {
							DataUploadClient client = new DataUploadClient(
									new URL(path));
							client.put(new StringBuffer(result), remotefile);
						} catch (IOException e) {
							_log.error("ERROR:" + e.getMessage());
						}
					} else if (destination.startsWith(CTW_PROTOCOL_TAG)) {
						String resp = URLEncoder.encode(result);
						send(resp);
						_log.info(result);
					}
				}
			}
		}
	}

	public String runScript(String cmd) {
		if (_cs == null) {
			_log.error("Console Service not set !"); //$NON-NLS-1$
		} else {
			return _cs.runCommand(cmd);
		}
		return null;
	}

	public void setConsoleService(ConsoleService service) {
		if (service != null) {
			try {
				_cs = service;
				service.setAlias("sysinfo", new String[] { "/management",
						"sysinfo" });
				service.setAlias("reboot", new String[] { "/management",
						"shell", "exec", "reboot" });
				service.setAlias("uninstall", new String[] { "/management",
						"uninstall" });
				service.setAlias("update", new String[] { "/management",
						"update" });
				service.setAlias("install", new String[] { "/management",
						"update" });
				service.setAlias("bundle", new String[] { "/management",
						"bundle" });
				service.setAlias("stop", new String[] { "/management", "stop" });
				service.setAlias("start",
						new String[] { "/management", "start" });
				service.setAlias("sync", new String[] { "/management",
						"synchronize" });
				service.setAlias("restart", new String[] { "/management",
						"restart" });
				service.setAlias("update", new String[] { "/management",
						"update" });
				service.setAlias("id", new String[] { "/management", "id" });
				service.setAlias("ss", new String[] { "/management", "status",
						"-c", "true" });
				service.setAlias("close", new String[] { "/management",
						"shutdown", "-t", "0" });
				service.setAlias("refresh", new String[] { "/management",
						"refresh" });
				service.setAlias("shutdown", new String[] { "/management",
						"shutdown" });

				service.setAlias("dep", new String[] { "/management",
						"dependency" });
				service.setAlias("ls", new String[] { "/management", "shell",
						"ls" });
				service.setAlias("dir", new String[] { "/management", "shell",
						"ls" });
				service.setAlias("cd", new String[] { "/management", "shell",
						"cd" });
				service.setAlias("mkdir", new String[] { "/management",
						"shell", "mkdir" });
				service.setAlias("pwd", new String[] { "/management", "shell",
						"pwd" });
				service.setAlias("cat", new String[] { "/management", "shell",
						"cat" });
				service.setAlias("zcat", new String[] { "/management", "shell",
						"zcat" });
				service.setAlias("exec", new String[] { "/management", "shell",
						"exec" });
				service.setAlias("run", new String[] { "/management", "shell",
						"exec" });
				service.setAlias("touch", new String[] { "/management",
						"shell", "touch" });
				service.setAlias("rm", new String[] { "/management", "shell",
						"rm" });

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void unsetConsoleService(ConsoleService service) {
		_cs = null;
	}

	public void setSyslog(org.avm.elementary.log4j.Logger syslog) {
		_syslog = syslog;
	}

	public void enableSyslog(String bindAddress, String hostAdress)
			throws Exception {
		if (_syslog == null) {
			throw new Exception("syslog service not available!");
		}
		_syslog.enableSyslog(bindAddress, hostAdress);
	}

	public void disableSyslog() throws Exception {
		if (_syslog == null) {
			throw new Exception("syslog service not available!");
		}
		_syslog.disableSyslog();
	}

	public void synchronize(PrintWriter out, boolean force) throws Exception {
		_log.debug("Synchronization...");
		_log.debug("-DownloadURL=" + getCurrentDownloadUrl());
		_log.debug("-UploadURL  =" + getCurrentUploadUrl());

		if (getManagementService() != null) {
			_log.debug("Begin sync bundles...");
			getManagementService().synchronize(out, force);
			_log.debug("End sync bundles...");
		} else {
			throw new Exception("Management service not available (null?)");
		}
		synchronizeData();
	}

	public boolean synchronizeData() throws Exception {
		boolean result = false;
		if (getCurrentUploadUrl() == null) {
			_log.debug("Cannot upload data : getUploadURL() == null");
			return false;
		}
		_log.debug("Upload data...");
		String home = System.getProperty("org.avm.home");
		if (home == null) {
			System.err.println("[Management] property org.avm.home not set ");
			return false;
		}

		Properties p = new Properties();
		try {
			String fp = home + "/data/upload";
			p.put("filepath", fp);
			p.put("remove", "true");
			execute("upload", p, new PrintWriter(System.out));
			result = true;

		} catch (IOException e) {
			System.err.println("[Management] ioexception error : "
					+ e.getMessage());
		} catch (CommandException e) {
			System.out.println("[Management] commandexception error : "
					+ e.getMessage());
		}
		return result;

	}

	public PackageAdmin getPackageAdminService() {
		ServiceReference sr = _context.getServiceReference(PackageAdmin.class
				.getName());
		return (PackageAdmin) _context.getService(sr);
	}

	public StartLevel getStartLevelService() {
		ServiceReference sr = _context.getServiceReference(StartLevel.class
				.getName());
		return (StartLevel) _context.getService(sr);
	}

	public void setDownloadUrl(URL url) throws Exception {
		Management managementService = getManagementService();
		if (managementService != null) {
			managementService.setDownloadUrl(url);
		} else {
			_log.error("Management service is null !");
		}
	}

	public void setUploadUrl(URL url) throws Exception {
		Management managementService = getManagementService();
		if (managementService != null) {
			managementService.setUploadUrl(url);
		} else {
			_log.error("Management service is null !");
		}
	}

	public URL getDownloadUrl(int mode) throws Exception {
		String surl = getManagementService().getDownloadUrl(mode);

		URL url = new URL(surl);
		return url;
	}

	public URL getUploadUrl(int mode) throws Exception {
		String surl = getManagementService().getUploadUrl(mode);

		URL url = new URL(surl);
		return url;
	}

	public void shutdown(PrintWriter out, int waittime, int exitCode)
			throws Exception {
		getManagementService().shutdown(out, waittime, exitCode);
	}

	public Management getManagementService() throws Exception {
		Object service = null;

		if (managementServiceReference != null) {
			service = _context.getService(managementServiceReference);
			if (service == null) {
				_context.ungetService(managementServiceReference);
			} else {
				return (Management) service;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("BundleContext=" + _context);
		}

		String managementServiceName = Management.class.getName();
		managementServiceReference = _context
				.getServiceReference(managementServiceName);

		if (_log.isDebugEnabled()) {
			_log.debug("ManagementServiceReference(" + managementServiceName
					+ ")=" + managementServiceReference);
		}

		if (managementServiceReference == null) {
			throw new Exception("Management Service Reference not available");
		} else {
			service = _context.getService(managementServiceReference);

			if (service == null) {
				throw new Exception("Management Service not available");
			}
		}

		return (Management) service;
	}

	public void setMessenger(Messenger messenger) {
		_messenger = messenger;
	}

	public void unsetMessenger(Messenger messenger) {
		_messenger = null;
	}

	public void send(String response) {
		org.avm.business.protocol.management.Management message = new org.avm.business.protocol.management.Management();
		message.setText(response);
		Hashtable d = new Hashtable();
		d.put("destination", "sam"); //$NON-NLS-1$ //$NON-NLS-2$
		d.put("binary", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			_messenger.send(d, message);
		} catch (Exception e) {
			_log.error("Error sendMessage", e); //$NON-NLS-1$
			_log.error(e); //$NON-NLS-1$
		}
	}

	public boolean isPrivateMode() throws Exception {
		return getManagementService().getCurrentMode() == Management.MODE_PRIVATE;
	}

	public void setPrivateMode(boolean b) throws Exception {
		try {
			if (b) {
				getManagementService().setCurrentMode(Management.MODE_PRIVATE);
			} else {
				getManagementService().setCurrentMode(Management.MODE_PUBLIC);
			}
		} catch (MalformedURLException e) {
			_log.error(e); //$NON-NLS-1$
		}
		ManagementPropertyFile configuration = ManagementPropertyFile
				.getInstance();
		configuration.setLastUpdateInPrivateZone(b);
	}

	public void sendBundleList(int mode) throws Exception {
		getManagementService().sendBundleList(mode);
	}

	public void setPublicMode() throws Exception {
		setPrivateMode(false);

	}

	public void setPrivateMode() throws Exception {
		setPrivateMode(true);

	}

	public int getCurrentMode() throws Exception {
		return getManagementService().getCurrentMode();
	}

	public URL getCurrentDownloadUrl() throws Exception {
		return getManagementService().getCurrentDownloadUrl();
	}

	public URL getCurrentUploadUrl() throws Exception {
		return getManagementService().getCurrentUploadUrl();
	}

	public void setCurrentMode(int mode) throws MalformedURLException,
			Exception {
		getManagementService().setCurrentMode(mode);
	}

}