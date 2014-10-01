package org.avm.elementary.common;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import EDU.oswego.cs.dl.util.concurrent.CondVar;
import EDU.oswego.cs.dl.util.concurrent.Mutex;

public abstract class AbstractManager implements ManageableService,
		ServiceTrackerCustomizer {

	protected Logger _log;

	protected boolean _started = false;

	protected BundleContext _context;

	private DataDeployer _data;

	private ServiceTracker _tracker;

	private Scheduler _scheduler;

	private boolean _deployed;

	private boolean _initialized;

	private Mutex _lock = new Mutex();

	private CondVar _initializedCondVar = new CondVar(_lock);

	private CondVar _deployedCondVar = new CondVar(_lock);

	protected static final SimpleDateFormat _frVdrDateExpFormater = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.FRANCE);

	protected static final SimpleDateFormat _defaultVdrDateExpFormater = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	private static final String URL_SEPARATOR = "/";

	public AbstractManager(BundleContext context, Logger log) throws Exception {
		_context = context;
		_log = log;
		_scheduler = new Scheduler();

		// create tracker
		String filter = "(&" + " (" + org.osgi.framework.Constants.OBJECTCLASS
				+ "=" + DataDeployer.class.getName() + ")" + " ("
				+ Constants.SERVICE_PID + "=" + getDeployerPID() + ")" + ")";

		Filter f = _context.createFilter(filter);
		_tracker = new ServiceTracker(_context, f, this);
	}

	protected abstract String getDeployerPID();

	private boolean acquireLock() {
		try {
			_lock.acquire();
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	public void start() {
		_tracker.open();
		if (acquireLock()) {

			try {
				// _lock.acquire();
				if (!_initialized) {
					_log.debug("Init manager: wait for manager initialized");
					_initializedCondVar.timedwait(1000);
					if (!_initialized) {
						_log.debug("Init manager:  time out initialisation ");
						return;
					}
				}
				_log.debug("Init manager: manager initialized");
				while (!_deployed) {
					_log.debug("Init manager: wait for data deployed");
					_deployedCondVar.await();
				}
				_log.debug("Init manager: data deployed");
			} catch (InterruptedException e) {
				_log.error(e.getMessage());
			} finally {
				// _lock.release();
				_started = true;
				_lock.release();
			}
		}
		undeploy(_data);

	}

	public void stop() {
		_tracker.close();
		_scheduler = null;
		_started = false;
	}

	public Object addingService(ServiceReference reference) {
		_data = (DataDeployer) _context.getService(reference);
		_log.debug("AddingService " + reference);
		if (acquireLock()) {
			// liberation du manager
			try {
				// _lock.acquire();
				_initialized = true;
				_initializedCondVar.signal();
			} // catch (InterruptedException e) {
				// _log.error(e.getMessage());
				// }
			finally {
				_lock.release();
			}
		}
		_scheduler.execute(new Deployer());
		return _data;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		_log.debug("ModifiedService " + reference);
	}

	public void removedService(ServiceReference reference, Object service) {
		_log.debug("RemovedService " + reference);
		_context.ungetService(reference);
	}

	protected abstract URL getUrlRepository();

	protected abstract void updateUrlRepository(URL url, String version);

	protected void deploy(DataDeployer data) {
		try {

			URL oldUrl = getUrlRepository();
			if (oldUrl == null) {
				return;
			}
			
			
			

			String oldPath = oldUrl.getPath();

			int iname = oldPath.lastIndexOf(URL_SEPARATOR);
			int iversion = oldPath.substring(0, iname).lastIndexOf(
					URL_SEPARATOR);
			int iroot = 0;

			String name = oldPath.substring(iname + 1);
			String oldTextVersion = oldPath.substring(iversion + 1, iname);
			Date oldVersion = _frVdrDateExpFormater.parse(oldTextVersion);
			Date newVersion = data.getVdrDateExp();
			String newTextVersion = _defaultVdrDateExpFormater
					.format(newVersion);
			String root = oldPath.substring(iroot, iversion);
			root = root.replace('/', File.separatorChar);

			File path = new File(root + File.separatorChar + oldTextVersion);
			String template = System.getProperty("org.avm.home")+File.separator+"data";
			if (!path.exists() && path.getAbsolutePath().indexOf(template) != -1) {
				String selectedFile = null;
				Date selected = new Date(0);
				String[] files = path.getParentFile().list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						File dir = new File(files[i]);
						String textVersion = dir.getName();
						try {
							Date version = _frVdrDateExpFormater
									.parse(textVersion);
							if (version.after(selected)) {
								selected = version;
								selectedFile = files[i];
								_log.debug("=>>>>>>>>>>>>>>Selected="
										+ selectedFile);
							}
						} catch (ParseException e) {
							_log.error("ParseException :" +e.getMessage());
						}
					}
				}
				_log.debug("=>>>>>>>>>>>>>>previous oldPath=" + oldPath);
				if (selectedFile != null) {
					oldPath = root+File.separator+path.getParentFile().getName()
							+ File.separator + selectedFile
							+ File.separator + name;
					newVersion = selected;
					newTextVersion = selectedFile;
					_log.debug("=>>>>>>>>>>>>>>newVersion=" + newVersion);
					String oldDir = oldPath.substring(iroot, iname);
					oldDir = oldDir.replace('/', File.separatorChar);
					String newPath = oldPath.substring(0, iversion) + URL_SEPARATOR
							+ newTextVersion + URL_SEPARATOR + name;
					URL newURL = new URL(oldUrl.getProtocol(), oldUrl.getHost(),
							oldUrl.getPort(), newPath);
					updateUrlRepository(newURL, data.getVdrId());
					return;
				}
			}

			String oldDir = oldPath.substring(iroot, iname);
			oldDir = oldDir.replace('/', File.separatorChar);
			String newPath = oldPath.substring(0, iversion) + URL_SEPARATOR
					+ newTextVersion + URL_SEPARATOR + name;
			URL newURL = new URL(oldUrl.getProtocol(), oldUrl.getHost(),
					oldUrl.getPort(), newPath);

			StringBuffer info = new StringBuffer();
			info.append(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS);
			info.append("\nOLD URL: " + oldUrl);
			info.append("\nNAME: " + name);
			info.append("\nROOT PATH: " + root);
			info.append("\nOLD VERSION: " + oldVersion);
			info.append("\nNEW VERSION: " + newVersion);
			info.append("\nNEW URL: " + newURL);
			info.append("\nOLD DIR: " + oldDir);
			info.append(org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);

			_log.info(info.toString());

			// deploy new version
			if (!oldVersion.equals(newVersion) && newVersion.after(oldVersion)) {
				_log.info(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
						+ " deploy version "
						+ _defaultVdrDateExpFormater.format(oldVersion)
						+ "  -> "
						+ _defaultVdrDateExpFormater.format(newVersion)
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);

				data.deploy(root);

				// reinit database & undeploy old version
				Date now = new Date();
				_log.info(org.avm.elementary.log4j.Constants.SETCOLOR_WARNING
						+ "\nDate - Heure systeme : "
						+ _frVdrDateExpFormater.format(now)
						+ "\nDate - Heure exploitation : "
						+ _frVdrDateExpFormater.format(data.getJexDateDeb())
						+ "\nDate - Heure fin exploitation : "
						+ _frVdrDateExpFormater.format(data.getJexDateFin())
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);

				if (now.after(data.getJexDateDeb())
						&& now.before(data.getJexDateFin())) {
					_log.info(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
							+ "Reinit url = "
							+ newURL
							+ " start = "
							+ _started
							+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);

					updateUrlRepository(newURL, data.getVdrId());

					if (!_started) {
						_log.info(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
								+ "Undeploy old version from "
								+ oldDir
								+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
						data.undeploy(oldDir);
					}
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	protected void undeploy(DataDeployer data) {
		class DatabaseFileFilter implements FileFilter {

			private Date _version;

			public DatabaseFileFilter(Date version) {
				_version = version;
			}

			public boolean accept(File pathname) {
				boolean result = false;
				Date version = null;
				try {
					version = _frVdrDateExpFormater.parse(pathname.getName());
				} catch (ParseException e) {
				}
				if (version != null && !version.equals(_version)
						&& version.before(_version))
					result = true;
				return result;
			}

		}

		try {
			URL url = getUrlRepository();
			if (url == null) {
				return;
			}
			String path = url.getPath();

			int iname = path.lastIndexOf(URL_SEPARATOR);
			int iversion = path.substring(0, iname).lastIndexOf(URL_SEPARATOR);
			int iroot = 0;

			String version = path.substring(iversion + 1, iname);

			String root = path.substring(iroot, iversion);
			root = root.replace('/', File.separatorChar);

			Date before = _frVdrDateExpFormater.parse(version);

			File dir = new File(root);
			File[] list = dir.listFiles(new DatabaseFileFilter(before));
			for (int i = 0; i < list.length; i++) {
				_log.info(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
						+ "Undeploy " + list[i]
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);

				data.undeploy(((File) list[i]).getAbsolutePath());

			}

		} catch (Exception e) {
			_log.error(e.getMessage());
		}

	}

	class Deployer implements Runnable {
		public void run() {
			deploy(_data);

			if (acquireLock()) {
				// liberation du manager
				try {
					// _lock.acquire();
					_deployed = true;
					_deployedCondVar.signal();
				}
				// catch (InterruptedException e) {
				// _log.error(e.getMessage());
				// }
				finally {
					_lock.release();
				}
			}
		}
	}

}