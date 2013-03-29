package org.avm.elementary.management.addons;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Scheduler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class PatchManagerActivator implements BundleActivator, Constants,
		ServiceTrackerCustomizer {

	public static final String DEPLOYED = "deployed";
	public static final String EXECUTED = "executed";
	public static final String MANAGEMENT_SCRIPT_EXECUTED = "management-script-executed";

	private Logger _log = Logger.getInstance(this.getClass());

	private BundleContext _context;

	private File _outputDir = null;

	private Scheduler _scheduler = new Scheduler();
	private ManagementService _management;
	private ServiceTracker _tracker;
	private Filter _filter;
	private ServiceRegistration _registration;

	public static final String FILTER = "("
			+ org.osgi.framework.Constants.OBJECTCLASS + "="
			+ ManagementService.class.getName() + ")";

	public void start(BundleContext context) throws Exception {
		_context = context;

		try {
			_tracker = new ServiceTracker(_context, getFilter(), this);
			_tracker.open();
			_log.debug("Tracker opened for " + _tracker.size() + " services");
		} catch (Throwable t) {
			_log.error("Error starting "
					+ _context.getBundle().getSymbolicName(), t);
		}

		Properties properties = new Properties();
		properties.put(SERVICE_PID, PatchManagerActivator.class.getName()
				+ ".PatchManager");
		_log.info("Register service :" + properties);
		_registration = _context.registerService(
				PatchManagerActivator.class.getName(), this, properties);
		_log.info("Registered.");
	}

	public void stop(BundleContext context) throws Exception {
		_registration.unregister();
	}

	private Filter getFilter() {
		_log.debug("Filter = " + FILTER);
		if (_filter == null) {
			try {
				_filter = _context.createFilter(FILTER);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
		return _filter;
	}

	private void runonce(String process, String params) {
		_log.info("Running '" + process + " " + params + "' ?"
				+ (isExecuted() == false));
		if (isExecuted() == false) {
			_log.info("Exec: " + params);
			org.avm.device.plateform.System.exec(process, params);
			executed();
			_log.info("End exec: " + params);
		} else {
			_log.info("Already executed: " + params);
		}
	}

	private void runonce(String managementscript) {
		_log.info("Running management " + managementscript + " ? "
				+ (isManagementScriptExecuted() == false));
		URL url;
		try {
			url = new URL("file://" + managementscript);
			if (isManagementScriptExecuted() == false) {
				_log.info("Runscript: " + url);
				runScript(url);
				managementScriptExecuted();
				_log.info("End exec: " + url);
			} else {
				_log.info("Management script already executed: " + url);
			}
		} catch (MalformedURLException e) {
			_log.error(e);
		}

	}

	public boolean isManagementScriptExecuted() {
		File file = new File(_outputDir + File.separator
				+ MANAGEMENT_SCRIPT_EXECUTED);
		return file.exists();
	}

	private void managementScriptExecuted() {
		File file = new File(_outputDir + File.separator
				+ MANAGEMENT_SCRIPT_EXECUTED);
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
	}

	public boolean isExecuted() {
		File file = new File(_outputDir + File.separator + EXECUTED);
		return file.exists();
	}

	private void executed() {
		File file = new File(_outputDir + File.separator + EXECUTED);
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
	}

	public boolean isDeployed() {
		File file = new File(_outputDir + File.separator + DEPLOYED);
		return file.exists();
	}

	private void deployed() {
		File file = new File(_outputDir + File.separator + DEPLOYED);
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
	}

	private void init() {
		String sdir = System.getProperty("org.avm.home") + File.separator
				+ "data" + File.separator + "patch";
		File dir = new File(sdir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String version = (String) _context.getBundle().getHeaders()
				.get("Bundle-Version");

		String[] content = dir.list();
		if (content != null) {
			for (int i = 0; i < content.length; i++) {
				if (!content[i].equals(version)) {
					System.out.println("Removing:" + content[i]);
					File oldDir = new File(dir + File.separator + content[i]);
					rmdir(oldDir);
				}
			}
		}

		sdir += File.separator + version;

		_outputDir = new File(sdir);
		if (!_outputDir.exists()) {
			_outputDir.mkdirs();
		}

	}

	private String deploy(URL url) {
		_log.info("Deploying file :" + url);
		final int BUFFER = 2048;
		byte data[] = new byte[BUFFER];
		int count;
		URLConnection conn;
		String out = null;
		try {
			conn = url.openConnection();
			InputStream is = (new BufferedInputStream(conn.getInputStream()));
			File f = new File(url.getFile());
			String filename = f.getName();
			File file = new File(_outputDir.getAbsolutePath() + File.separator
					+ filename);
			_log.info("Creating file :" + file.getAbsolutePath());
			out = file.getAbsolutePath();
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

			while ((count = is.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();

			is.close();
			_log.info("File Created :" + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out;
	}

	private void rmdir(File dir) {

		if (dir.isDirectory()) {
			String[] list = dir.list();
			for (int i = 0; i < list.length; i++) {
				File f = new File(dir, list[i]);
				rmdir(f);
			}
			_log.debug("Remove " + dir + " directory");
			dir.delete();
			return;
		}

		_log.debug("Remove " + dir + " file");
		dir.delete();
		return;
	}

	public Object addingService(ServiceReference reference) {
		_log.debug("addingService ...:" + reference);
		_log.debug("context:" + _context);
		Object service = _context.getService(reference);
		_log.debug("Service name: " + service.getClass().getName());
		if (service instanceof ManagementService) {
			_management = (ManagementService) service;
		}

		_scheduler.execute(new Runnable() {

			public void run() {
				_log.info("Running patch........................................");
				init();

				String plateform = System.getProperty("org.avm.plateform");

				// --debut [DLA] astuce necessaire pour la MAJ des (vieux!)
				// fm6000
				if (plateform == null) {
					String osname = System.getProperty("os.name");
					if (osname != null && osname.equalsIgnoreCase("Windows CE")) {
						plateform = "fm6000";
					} else {
						plateform = "unknown";
					}
				}// --fin

				String shellScript = "runonce.sh";

				String options = "";
				String managementscript = "management.script";
				String process = "/bin/sh";
				String ext = ".sh";
				if (plateform.equalsIgnoreCase("fm6000")) {
					options = "/K";
					ext = ".bat";
					process = "cmd.exe";
					shellScript = "runonce.bat";
				}

				if (!isDeployed()) {
					_log.info("Deploying Patch...");
					_log.info("Context:" + _context);
					Enumeration iter = _context.getBundle().findEntries(
							"/patch", "*", true);
					if (iter != null) {
						while (iter.hasMoreElements()) {
							URL url = (URL) iter.nextElement();
							if (_log.isDebugEnabled()) {
								_log.debug("Entry: " + url);
							}
							String file = deploy(url);
							if (file != null
									&& file.toLowerCase().indexOf("runonce") != -1
									&& file.toLowerCase().endsWith(ext)) {
								shellScript = file;
							}

							if (file != null
									&& file.indexOf("management.script") != -1) {
								managementscript = file;
							}

						}
						if (shellScript != null) {
							deployed();
						}
						_log.info("Patch deployed...");
					}

				} else {
					_log.info("Patch already deployed...");
					if (managementscript != null) {
						managementscript = _outputDir.getAbsolutePath()
								+ File.separator + managementscript;
					}
					if (shellScript != null) {
						shellScript = _outputDir.getAbsolutePath()
								+ File.separator + shellScript;
					}
				}

				String shellScriptParam = _outputDir.getAbsolutePath();

				runonce(process, options + " " + shellScript + " "
						+ shellScriptParam);

				runonce(managementscript);

			}

		});
		return service;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		_log.debug("modifiedService ...");
		_log.debug("..." + service.getClass().getName());
		if (service instanceof ManagementService) {
			_management = (ManagementService) service;
		}

	}

	public void removedService(ServiceReference reference, Object service) {
		_log.debug("removedService ...");
		_log.debug("..." + service.getClass().getName());
		if (service instanceof ManagementService) {
			_management = null;
		}
	}

	private void runScript(URL url) {
		if (_management != null) {
			_management.runScript(url);
		}
	}

}
