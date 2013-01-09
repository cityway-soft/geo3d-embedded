package org.avm.elementary.management.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * 
 * @author didier.lallemand@mercur.fr
 * 
 */
public class ManagementImpl implements Management {
	private static int MAX_RETRY;

	private static final String AUTOUPDATE_TAG = Management.class.getPackage()
			.getName() + ".autoupdate";

	private int _retry = 1;

	private BundleContext _context;

	private SynchronizeBundleThread _synchronizeBundlesThread;

	private Management _instance;

	private StartLevel _startLevelService = null;

	private PackageAdmin _packageAdminService = null;

	private URL _downloadURL;

	private URL _uploadURL;

	private Timer _shutdownTimer;

	/**
	 * @param context
	 * 
	 */
	public ManagementImpl() {
		_instance = this;
	}

	public void start() {
		boolean update = System.getProperty(AUTOUPDATE_TAG, "true").equals(
				"true");
		init();
		try {
			synchronize(update, new PrintWriter(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		unsynchronize();
	}

	public void setBundleContext(BundleContext context) {
		_context = context;
		setVersion();
	}

	private void setVersion() {
		double ver = 0;
		String version = null;

		for (int i = 0; i < _context.getBundles().length; i++) {
			Bundle bundle = _context.getBundles()[i];
			String sver = (String) bundle.getHeaders().get("Bundle-Version");
			double v = formatVersion(sver);
			if ((bundle.getSymbolicName().indexOf("org.angolight") != -1 || bundle
					.getSymbolicName().indexOf("org.avm") != -1)
					&& !bundle.getSymbolicName().endsWith((".data"))) {
				if (v > ver) {
					ver = v;
					version = sver;
				}
			}
		}
		if (version != null) {
			System.setProperty(AVMVERSION_TAG, version);
		}

	}

	/**
	 * converti un numero de version au format 1.0.0.1.2 dans un format
	 * numérique 1.0012 (pour pouvoir effectuer des comparaisons)
	 * 
	 * @param version
	 * @return
	 */
	private double formatVersion(String version) {
		if (version == null)
			return Double.NaN;
		StringBuffer buf = new StringBuffer();
		boolean dotfound = false;
		for (int i = 0; i < version.length(); i++) {
			char c = version.charAt(i);
			if (Character.isDigit(c)) {
				buf.append(c);
			} else if (dotfound == false && c == '.') {
				dotfound = true;
				buf.append(c);
			}
		}
		double ver;
		try {
			ver = Double.parseDouble(buf.toString());
		} catch (NumberFormatException e) {
			ver = 0;
		}
		return ver;
	}

	
	public void init() {
		System.out.println("Init management...");

		// -- load property files
		File dir = new File(System.getProperty(AVMHOME_TAG)
				+ System.getProperty("file.separator") + "bin");
		if (dir.exists()) {
			PropertyFileFilter filter = new PropertyFileFilter();
			File[] list = dir.listFiles(filter);
			for (int i = 0; i < list.length; i++) {
				loadProperties(list[i].getAbsolutePath());
			}
		}

		// -- set default download & upload URL
		try {
			setDownloadURL(null);
			setUploadURL(null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// -- check package and refresh if necessairy
		PrintWriter out = new PrintWriter(System.out);
		if (checkBundlePackages(_context, out, null) == false) {
			out.println("!!!!!!!!!!!!!!!!!!!!    WARNING    !!!!!!!!!!!!!!!!!!!!!!!!");
			out.println("!              Packages failure detected                  !");
			out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Bundle[] bundles = new Bundle[_context.getBundles().length - 1];
			int j = 0;
			for (int i = 0; i < _context.getBundles().length; i++) {
				Bundle bundle = _context.getBundles()[i];
				// -- refresh all bundles, except management
				if (!bundle.getSymbolicName().equals(
						Management.class.getPackage().getName())) {
					bundles[j] = bundle;
					j++;
				}
			}
			refresh(bundles, out);
		} else {
			out.println("[DLA] Packages seem ok.");
		}

	}

	public void setUploadURL(URL url) throws MalformedURLException {
		if (url == null) {
			String defaultUrl = System.getProperty(PRIVATE_UPLOAD_URL_TAG,
					"ftp://avm:avm++@ftpserver.avm.org/upload");
			try {
				_uploadURL = new URL(System.getProperty(
						Management.DEFAULT_UPLOAD_URL_TAG, defaultUrl));
			} catch (Throwable t) {
				_uploadURL = null;
			}
		} else {
			_uploadURL = url;
		}
	}

	public void setDownloadURL(URL url) throws MalformedURLException {
		if (url == null) {
			String defaultUrl = System.getProperty(
					PRIVATE_DOWNLOAD_URL_TAG,
					"ftp://avm:avm++@ftpserver.avm.org/"
							+ System.getProperty("org.avm.plateform")
							+ "/$e/$v/bundles");
			try {
				_downloadURL = new URL(System.getProperty(
						Management.DEFAULT_DOWNLOAD_URL_TAG, defaultUrl));
			} catch (Throwable t) {
				_downloadURL = null;
			}
		} else {
			_downloadURL = url;
		}

	}

	public URL getUploadURL() {
		return _uploadURL;
	}

	public URL getDownloadURL() {
		return _downloadURL;
	}

	public void synchronize(PrintWriter out) throws Exception {
		synchronize(true, out);
	}

	public void synchronize(boolean update, PrintWriter out) throws Exception {
		_retry = 1;
		MAX_RETRY = Integer.parseInt(System.getProperty(
				Management.class.getPackage() + ".autoupdate.maxtry", "5"));

		if (_synchronizeBundlesThread == null
				|| _synchronizeBundlesThread.isAlive() == false) {

			_synchronizeBundlesThread = new SynchronizeBundleThread(update, out);
			_synchronizeBundlesThread.start();
		} else {
			throw new Exception(
					"Management synchronisation already in progress...");
		}
	}

	public void unsynchronize() {
		if (_synchronizeBundlesThread != null) {
			_synchronizeBundlesThread.stop();
			_synchronizeBundlesThread = null;
		}
	}

	/**
	 * Demarre tous les bundles
	 * 
	 * @param out
	 */
	public void startAllBundles(PrintWriter out) {
		Bundle[] bundles = _context.getBundles();

		for (int i = 0; i < bundles.length; i++) {
			Bundle b = bundles[i];
			try {
				boolean fragment = (b.getHeaders().get("Fragment-Host") != null);
				if (fragment) {
					out.println("[WARNING] Bundle " + b.getSymbolicName()
							+ " is a fragment (will not be started)");
				} else if (b.getState() != Bundle.STARTING
						&& b.getState() != Bundle.ACTIVE) {
					b.start();
				}
			} catch (Throwable t) {
				out.println("failed (" + t.getLocalizedMessage() + ")");
			}
		}

	}

	class SynchronizeBundleThread implements Runnable {
		private Thread _thread = null;

		private PrintWriter _out = null;

		private boolean _update;

		public SynchronizeBundleThread(boolean update, PrintWriter out) {
			_out = out;
			_update = update;
		}

		public void start() {
			if (_thread == null) {
				_thread = new Thread(this);
				_thread.setName("[AVM] management core");
				_thread.start();
			}
		}

		public void stop() {
			if (_thread != null) {
				_thread.interrupt();
				_thread = null;
			}
		}

		public boolean isAlive() {
			if (_thread != null) {
				return _thread.isAlive();
			} else {
				return false;
			}
		}

		private void debug(String trace) {
			if (DEBUG) {
				_out.println("[ManagementCore] " + trace);
			}
		}

		private boolean isUpdateForced() {
			boolean result = false;
			URL url = null;
			try {
				url = new URL(System.getProperty("osgi.configuration.area")
						+ "avm.deployed");
				File file = new File(url.getFile());
				result = (file.exists() == false);
			} catch (MalformedURLException e1) {
				result = true;
			}
			return result;
		}

		public void run() {
			int timeBeforeRetry = 3; // secondes
			boolean updateForced = false;

			if (isUpdateForced()) {
				try {
					setDownloadURL(new URL(
							System.getProperty(PRIVATE_DOWNLOAD_URL_TAG)));
					setUploadURL(new URL(
							System.getProperty(PRIVATE_UPLOAD_URL_TAG)));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateForced = true;
				_update = true;
				MAX_RETRY = Integer.MAX_VALUE;
			}
			if (getDownloadURL() == null) {
				_out.println("Download URL is null : update operation is cancelled.");
				return;
			}
			_out.println("Download url : " + getDownloadURL());
			_out.println("Upload url   : " + getUploadURL());
			_out.flush();

			while (_retry <= MAX_RETRY && _thread.isInterrupted() == false) {
				debug("********************** " + _retry + "/" + MAX_RETRY
						+ " **********************");
				try {
					long t0 = System.currentTimeMillis();
					SynchronizeBundlesCommand cmd = new SynchronizeBundlesCommand(
							_update, _context, _instance, _out);
					cmd.exec();
					long time = System.currentTimeMillis() - t0;
					debug("auto update [done] (" + time + " ms.)");
					if (updateForced) {
						URL url = new URL(
								System.getProperty("osgi.configuration.area")
										+ "avm.deployed");
						FileOutputStream out = new FileOutputStream(
								url.getFile());
						out.write(new Date().toString().getBytes());
						out.close();
					}

					break;
				} catch (IOException ioe) {
					_out.println("[ERROR] AutoUpdate IOException : "
							+ ioe.getMessage());
					int cpt = 0;
					_out.println("[INFO] management.core - Retry in "
							+ timeBeforeRetry + " sec : ");
					while (cpt < timeBeforeRetry) {
						try {
							Thread.sleep(1000 + (_retry - 1) * 2000);
						} catch (InterruptedException e) {
							break;
						}
						cpt++;
					}
					_retry++;
				}

			}

			startAllBundles(_out);
			_out.flush();

			_synchronizeBundlesThread = null;
		}
	}

	private void loadProperties(String filename) {
		Properties p;
		p = new Properties();
		try {
			p.load(new FileInputStream(filename));
			System.out.println("Loading properties : " + filename);
			System.out.println(p);
			Enumeration e = p.keys();
			boolean changed = false;
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = p.getProperty(key);
				String currentValue = System.getProperty(key);
				if (currentValue != null && !currentValue.equals(value)) {
					changed = true;
					p.setProperty(key, currentValue);
				} else {
					System.setProperty(key, value);
				}
			}
			if (changed) {
				p.store(new FileOutputStream(filename),
						"Modified by Management Core");
			}

		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException (" + e.getMessage()
					+ "): " + filename);
		} catch (IOException e) {
			System.err.println("IOException (" + e.getMessage() + "): "
					+ filename);
		}
	}

	public StartLevel getStartLevelService() {
		ServiceReference sr = _context.getServiceReference(StartLevel.class
				.getName());
		_startLevelService = (StartLevel) _context.getService(sr);
		return _startLevelService;
	}

	public PackageAdmin getPackageAdminService() {
		ServiceReference sr = _context.getServiceReference(PackageAdmin.class
				.getName());
		_packageAdminService = (PackageAdmin) _context.getService(sr);
		return _packageAdminService;
	}

	protected void refresh(Bundle[] bundles, PrintWriter out) {
		try {
			getPackageAdminService().refreshPackages(bundles);
			getPackageAdminService().resolveBundles(bundles);
			// if (DEBUG){
			// out.println("[DLA] Packages refreshed.");
			// }
		} catch (Throwable t) {
			out.println("[ManagementCore] Error on refresh : " + t.getMessage());
		}
	}

	protected boolean checkBundlePackages(BundleContext context,
			PrintWriter out, Bundle bundle) {
		org.osgi.framework.ServiceReference packageAdminRef = context
				.getServiceReference("org.osgi.service.packageadmin.PackageAdmin"); //$NON-NLS-1$
		boolean result = true;
		long t0 = System.currentTimeMillis();
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
		out.println("[DLA] Time to check packages : "
				+ (System.currentTimeMillis() - t0) + "ms.");

		return result;
	}

	public void shutdown(PrintWriter out, int waittime, int exitCode) {
		try {
			if (_shutdownTimer == null) {
				out.println("Shutdown in " + waittime + " secondes.");
				_shutdownTimer = new Timer();
				_shutdownTimer.schedule(new ShutdownTimerTask(exitCode, out),
						waittime * 1000);
				// si pas d'arret au bout de n secondes...
				// _exitTimer = new Timer();
				// _exitTimer.schedule(new ExitTask(exitCode),
				// (waittime + 60) * 1000);
			}

			if (waittime == -1 && _shutdownTimer != null) {
				out.println("Cancel shutdown secondes.");
				_shutdownTimer.cancel();
				_shutdownTimer = null;
			}
		} catch (Exception e) {
			out.println("ERROR: " + e.getMessage());
		}
	}

	class ShutdownTimerTask extends TimerTask {
		PrintWriter _out;
		private int _exitCode;

		public ShutdownTimerTask(int exitCode, PrintWriter out) {
			_out = out;
			_exitCode = exitCode;
		}

		public void run() {
			_out.println("Shutdown...");
			halt(_exitCode);
		}

		private void halt(final int exitCode) {
			BundleListener listener = new BundleListener() {
				public void bundleChanged(BundleEvent event) {
					Bundle[] bundles = _context.getBundles();
					int active = 0;
					for (int i = 0; i < bundles.length; i++) {
						if (bundles[i].getState() == Bundle.ACTIVE) {
							active++;
						}
					}
					System.out.println("[ManagementCore] Active bundles : "
							+ active);
					if (active == 2) { // Bundle SystemOsgi + Bundle Management
						_out.println("bye bye...");
						System.exit(exitCode);
					}

					try {
						Thread.sleep(5 * 1000);
						_out.println("force exit...");
						System.exit(exitCode);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};
			_context.addBundleListener(listener);
			int managementStartLevel = getManagementStartLevel();
			getStartLevelService().setStartLevel(managementStartLevel);
		}
	}

	private int getManagementStartLevel() {
		Bundle bundle = _context.getBundle();
		return getStartLevelService().getBundleStartLevel(bundle);
	}

	class PropertyFileFilter implements FilenameFilter {
		public boolean accept(File dir, String filename) {
			return filename.toLowerCase().endsWith(".properties");
		}
	}

}