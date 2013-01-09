package org.avm.elementary.management.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.service.startlevel.StartLevel;

class SynchronizeBundlesCommand implements BundleListener {

	private BundleContext _context;

	private Management _management;

	private boolean _update;

	private static PrintWriter _out = null;

	private Timer _timerWaitForEndOfUpdateProcess;

	private EndOfProcessTask _task;

	private boolean _fwkNeedRestart;

	public SynchronizeBundlesCommand(boolean update, BundleContext context,
			Management management, PrintWriter out) {
		_context = context;
		_management = management;

		_out = out;
		_update = update;
		_timerWaitForEndOfUpdateProcess = new Timer();
		_task = new EndOfProcessTask();
	}

	private static void println(String trace) {
		if (_out != null) {
			_out.println("[ManagementCore] " + trace);
		}
	}

	private static void print(String trace) {
		if (_out != null) {
			_out.print("[ManagementCore] " + trace);
		}
	}

	private static void flush() {
		if (_out != null) {
			_out.flush();
		}
	}

	public void exec() throws IOException {

		try {
			if (_update) {
				println("*     * A U T O M A T I C * U P D A T E *         *");
				println("***************************************************");
				flush();
				deploy();
			}
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			println("!!!!!!!!!!!! => Erreur au cours du deploiement :");
			e.printStackTrace(_out);
			println("==============================================================");
		} finally {

		}
	}

	private void sendReport() {
		BundleList bundleList = createFromFwk();
		Enumeration enumeration = bundleList.elements();
		StringBuffer buffer = new StringBuffer();
		while (enumeration.hasMoreElements()) {
			BundleProperties element = (BundleProperties) enumeration
					.nextElement();
			buffer.append(element);
			buffer.append(System.getProperty("line.separator"));
		}

		String filename = "$e_$v_$i_updateauto.log";
		String path = _management.getUploadURL().toString();
		
		
		String terminalName = Terminal.getInstance().getName();
		String terminalOwner = Terminal.getInstance().getOwner();
		String terminalPlateform =  Terminal.getInstance().getPlateform();
		String terminalId =  Terminal.getInstance().getId();

		String surl = Utils.formatURL(path + "/" + filename, terminalId, terminalName,
				terminalOwner, terminalPlateform);

		try {
			SimpleFTPClient client = new SimpleFTPClient();
			client.put(new URL(surl), buffer);
		} catch (IOException e) {
			println("IOException (sendReport) :" + e.getMessage());
		}
	}

	private void stopBundlesToUpdate(BundleList list) {
		Enumeration e = list.elements();
		while (e.hasMoreElements()) {
			BundleProperties bp = (BundleProperties) e.nextElement();
			if (bp.getStartlevel() > 0) {
				String bundleName = bp.getName();
				Bundle bundle = getBundle(bundleName);

				boolean isManagementBundle = this.getClass().getPackage()
						.getName().indexOf(bundleName) != -1;

				if (isManagementBundle) {
					// -- attention : ne pas stopper le bundle management (sinon
					// redemarrage manuel!)
					println(" update cannot stop management!!!!! ");
					flush();
					continue;
				}
				if (bundle != null) {
					try {
						print("[                   Stopping " + bundleName);
						flush();
						bundle.stop();
						println("     OK ]");
					} catch (BundleException e1) {
						println("Error on stop bundle : " + bundleName);
					}
					flush();
				}
			}
		}
	}

	public void deploy() throws IOException, BundleException {
		String managementNeedToBeUpdated = null;

		BundleList bundleList = downloadBundleList();
		if (bundleList == null) {
			String url1 = getBundleListURL(BundleList.FILENAME, false);
			println("Failed to get bundles list : " + url1);
			String url2 = getBundleListURL(BundleList.FILENAME, true);
			println("Failed to get default bundles list : " + url2);
			throw new BundleException("Cannont get bundle.list from " + url1
					+ " or " + url2);
		}

		updateStartLevel(bundleList);
		bundleList = getBundlesToUpdate(bundleList);

		println("##########################################");
		println(bundleList.toString());
		println("######### TOTAL : " + bundleList.size()
				+ "######################");
		flush();
		stopBundlesToUpdate(bundleList);
		Vector updatedBundles = new Vector();

		boolean updated;
		_context.removeBundleListener(this);
		_context.addBundleListener(this);

		if (bundleList.size() > 0) {
			Enumeration e = bundleList.elements();
			while (e.hasMoreElements()) {
				updated = false;
				BundleProperties bundleProperties = (BundleProperties) e
						.nextElement();
				int bundleStartlevel = bundleProperties.getStartlevel();

				String bName = bundleProperties.getName();

				Bundle bundle = getBundle(bName);

				try {
					if (bundleStartlevel < 0) {
						if (bundle != null) {
							bundle.uninstall();
						}
						continue;
					} else if (bundleStartlevel > 0) {
						if (bundle == null) {
							String msg = "[update]" + bName + " INSTALL";
							println(msg);
							flush();
							install(bundleProperties, bName);
							updated = true;
						} else {
							String msg = "[update]" + bName + " UPDATE";
							println(msg);
							flush();
							boolean isBundleWithNativeCode = (bundle
									.getHeaders().get("Bundle-NativeCode") != null);
							_fwkNeedRestart = (_fwkNeedRestart || isBundleWithNativeCode);
							if (isBundleWithNativeCode) {
								println("Bundle '"
										+ bName
										+ "' embed native code ; Framework will be restarted after update");
								if (bundle != null) {
									bundle.uninstall();
									continue;
								}
							}

							if (_management.getClass().getPackage().getName()
									.indexOf(bName) != -1) {
								managementNeedToBeUpdated = bName;
								continue;
							}

							update(bName);
							updated = true;
						}
						if (updated) {
							updatedBundles.addElement(bName);
						}
					}
				} catch (Throwable ex) {
					println("ERR " + bName + " : " + ex.getMessage());
					bundle = getBundle(bName);
					updatedBundles.addElement(bName);
					ex.printStackTrace();
				}
			}

		}

		((ManagementImpl) _management).refresh(null, _out);

		sendReport();

		if (_fwkNeedRestart) {
			URL url = new URL(System.getProperty("osgi.configuration.area")
					+ "avm.deployed");
			File file = new File(url.getFile());
			if (file.exists()) {
				file.delete();
			}
			_management.shutdown(_out, Management.TIME_TO_FWK_SHUTDOWN,
					Management.EXITCODE_REBOOT_PLATEFORM);
		}

		if (managementNeedToBeUpdated != null) {
			update(managementNeedToBeUpdated);
		}

		_timerWaitForEndOfUpdateProcess = new Timer();
		_task = new EndOfProcessTask();
		_timerWaitForEndOfUpdateProcess.schedule(_task, 2000);

	}

	private void setBundleStartLevel(Bundle bundle, int startlevel) {

		StartLevel sl = _management.getStartLevelService();
		int currentStartLevel = sl.getBundleStartLevel(bundle);
		if (sl != null && bundle != null) {
			boolean isManagementBundle = this.getClass().getPackage().getName()
					.indexOf(bundle.getSymbolicName()) != -1;
			if (isManagementBundle) {
				println("**WARNING** Cannot change management startlevel !");
				return;
			}
			println("[update] Update " + bundle.getSymbolicName()
					+ " startlevel (" + currentStartLevel + "=>" + startlevel
					+ ")");
			sl.setBundleStartLevel(bundle, startlevel);
		}
	}

	private void update(String bundleName) throws IOException, BundleException {
		URLConnection connection = getRemoteBundleURLConnection(bundleName);
		println("Download " + connection.getURL());
		Bundle bundle = getBundle(bundleName);
		bundle.update(connection.getInputStream());
	}

	/**
	 * installation des bundles
	 * 
	 * @param management
	 * @param context
	 * @param bundleProperties
	 * @param bundleName
	 * @param out
	 * @param buffer
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	private void install(BundleProperties bundleProperties, String bundleName)
			throws IOException, BundleException {
		URLConnection connection = getRemoteBundleURLConnection(bundleName);
		if (connection != null) {
			println("Downloading " + connection.getURL());
			Bundle bundle = _context.installBundle(connection.getURL()
					.toString(), connection.getInputStream());
			setBundleStartLevel(bundle, bundleProperties.getStartlevel());
		} else {
			println("Error cannot download bundle " + bundleName + "  !!!!");
		}
	}

	public static String stateInt2String(int state) {
		switch (state) {
		case Bundle.ACTIVE:
			return "ACTIVE";
		case Bundle.INSTALLED:
			return "INSTALLED";
		case Bundle.RESOLVED:
			return "RESOLVED";
		case Bundle.STARTING:
			return "STARTING";
		case Bundle.STOPPING:
			return "STOPPING";
		case Bundle.UNINSTALLED:
			return "UNINSTALLED";
		default:
			return "";
		}
	}

	/**
	 * converti un numero de version au format 1.0.0.10.2 dans un format
	 * numérique 1.00102 (pour pouvoir effectuer des comparaisons)
	 * 
	 * @param version
	 * @return
	 */
	private double getVersion(String version) {
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
			ver = Double.MAX_VALUE;
		}
		return ver;
	}

	/**
	 * retourne une hashtable contenant la liste des bundles sur le repository
	 * 
	 * @return
	 * @throws IOException
	 * @throws IOException
	 * @throws IOException
	 */
	private BundleList downloadBundleList() throws IOException {
		BundleList bundleList = null;
		bundleList = getBundleList(BundleList.FILENAME
				+ BundleList.COMPRESSED_EXT);
		if (bundleList == null) {
			bundleList = getBundleList(BundleList.FILENAME);
		}
		return bundleList;
	}

	private String getBundleListURL(String filename, boolean defaut) {
		String terminalName =  Terminal.getInstance().getName();
		String terminalId =  Terminal.getInstance().getId();
		String terminalOwner =  Terminal.getInstance().getOwner();
		String plateform =  Terminal.getInstance().getPlateform();
		
		String strurl = _management.getDownloadURL() + "/" + filename;
		
		if (defaut) {
			terminalId = "default";
			terminalName = "default";
		}
		strurl = Utils.formatURL(strurl, terminalId, terminalName, terminalOwner, plateform);
		return strurl;
	}

	private BundleList getBundleList(String filename) {

		String strurl = getBundleListURL(filename, false);
		BundleList bundleList = null;

		try {
			bundleList = loadFromURL(strurl);
		} catch (IOException e) {
		}

		if (bundleList == null) {
			strurl = getBundleListURL(filename, true);
			try {
				bundleList = loadFromURL(strurl);
			} catch (IOException e) {
			}
		}
		return bundleList;
	}

	private BundleList getBundlesToUpdate(BundleList bundleList) {
		flush();
		try {
			println("[update] BundleList count : " + bundleList.size());

			Bundle[] bundles = _context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				String bundleName = bundles[i].getSymbolicName();

				BundleProperties bundleProperties = bundleList
						.getBundleProperties(bundleName);
				if (bundleProperties == null) {
					continue;
				}

				String ver = bundleProperties.getVersion();
				double bundleVersion = getVersion(ver);
				ver = (String) bundles[i].getHeaders().get("Bundle-Version");
				double currentVersion = getVersion(ver);

				// -- on retire les bundles dont la version est plus recente que
				// celle du bundles.list
				if (bundleVersion <= currentVersion
						&& bundleProperties.getStartlevel() >= 0) {
					bundleList.remove(bundleName);
				}
			}
			flush();

			Enumeration e = bundleList.elements();
			while (e.hasMoreElements()) {
				BundleProperties bl = (BundleProperties) e.nextElement();
				String bundlename = bl.getCompleteName();
				Bundle bundle = getBundle(bundlename);
				// -- on retire les bundles dont le startlevel est negatif
				if (bundle == null && bl.getStartlevel() <= 0) {
					bundleList.remove(bundlename);
				}
			}
		} catch (Throwable t) {
			println("!!!!!!!!!!!! => Erreur au cours du downloadBundleList :");
			t.printStackTrace(_out);
			println("==============================================================");
		}
		return bundleList;

	}

	private void updateStartLevel(BundleList bundleList) {
		try {
			Bundle[] bundles = _context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				String bundleName = bundles[i].getSymbolicName();

				BundleProperties bundleProperties = bundleList
						.getBundleProperties(bundleName);
				if (bundleProperties == null) {
					continue;
				}

				int newStartLevel = bundleProperties.getStartlevel();

				if (bundleProperties.isEnable() == false) {
					newStartLevel = 999; // disabled
				}

				int currentStartLevel = getBundleStartLevel(bundles[i]);
				boolean startLevelChanged = (currentStartLevel == -1) ? false
						: (currentStartLevel != newStartLevel);

				if (newStartLevel > 0 && startLevelChanged) {
					setBundleStartLevel(bundles[i], newStartLevel);
				}
			}
			flush();

		} catch (Throwable t) {
			println("!!!!!!!!!!!! => Erreur au cours du updateStartLevel :");
			t.printStackTrace(_out);
			println("==============================================================");
		}

	}

	private int getBundleStartLevel(Bundle bundle) {
		int result = -1;
		if (_management.getStartLevelService() != null) {
			result = _management.getStartLevelService().getBundleStartLevel(
					bundle);
		}
		return result;
	}

	private BundleList loadFromURL(String strurl) throws IOException {
		URL url = new URL(strurl);
		BundleList bundleList = BundleList.load(url);
		return bundleList;
	}

	private BundleList createFromFwk() {
		BundleList bundleList = new BundleList();

		Bundle[] bundles = _context.getBundles();
		for (int i = 1; i < bundles.length; i++) {
			BundleProperties bp = new BundleProperties();
			String bundleName = bundles[i].getSymbolicName();
			String bundleVersion = (String) bundles[i].getHeaders().get(
					"Bundle-Version");
			bundleList.put(bundleName, bp);
			bp.setName(bundleName);
			bp.setVersion(bundleVersion);

			bp.setStartlevel(_management.getStartLevelService()
					.getBundleStartLevel(bundles[i]));
		}
		return bundleList;
	}

	protected Bundle getBundle(String bundlename) {

		try {
			Bundle[] bundles = _context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				Bundle bundle = bundles[i];
				String installbundle = (String) bundle.getHeaders().get(
						"Bundle-SymbolicName");
				installbundle = formatBundleName(installbundle);
				if (installbundle != null && installbundle.equals(bundlename)) {
					return bundle;
				}
			}
		} catch (Exception e) {
		}

		return null;
	}

	public String formatBundleName(String bundlename) {
		String result = bundlename;
		if (bundlename != null) {
			int idx = bundlename.indexOf(";");
			if (idx != -1) {
				result = bundlename.substring(0, idx);
			}
		}
		return result;
	}

	private URLConnection getRemoteBundleURLConnection(String bundlename)
			throws MalformedURLException {
		String downloadURL = _management.getDownloadURL().toString();

		String terminalName =  Terminal.getInstance().getName();

		String terminalOwner = Terminal.getInstance().getOwner();

		String terminalPlateform =  Terminal.getInstance().getPlateform();
		
		String terminalId =  Terminal.getInstance().getId();


		StringBuffer buf = new StringBuffer();
		buf.append(downloadURL);
		buf.append("/");
		buf.append(bundlename);
		buf.append(".jar");

		String surl = Utils.formatURL(buf.toString(), terminalId, terminalName,
				terminalOwner, terminalPlateform);
		URL url = new URL(surl);
		URLConnection connection = null;
		try {
			connection = url.openConnection();
			check(connection);
			return connection;
		} catch (IOException e) {

		}

		surl = Utils.formatURL(buf.toString(), "default", "default",
				terminalOwner, terminalPlateform);
		url = new URL(surl);
		try {
			connection = url.openConnection();
			check(connection);
			return connection;
		} catch (IOException e) {
			println("IOException " + e.getMessage());
		}
		return null;
	}

	private void check(URLConnection c) throws IOException {
		c.getInputStream();
	}

	private String bundleState2String(int state) {
		switch (state) {
		case BundleEvent.INSTALLED:
			return "INSTALLED";
		case BundleEvent.RESOLVED:
			return "RESOLVED";
		case BundleEvent.STARTED:
			return "STARTED";
		case BundleEvent.STARTING:
			return "STARTING";
		case BundleEvent.STOPPED:
			return "STOPPED";
		case BundleEvent.STOPPING:
			return "STOPPING";
		case BundleEvent.UNINSTALLED:
			return "UNINSTALLED";
		case BundleEvent.UNRESOLVED:
			return "UNRESOLVED";
		case BundleEvent.UPDATED:
			return "UPDATED";

		default:
			return "unknown. ";
		}
	}

	public void bundleChanged(BundleEvent bundleEvent) {
		if (bundleEvent.getType() == BundleEvent.RESOLVED) {
			// println(new Date() + " - Reschedule refresh process....");
			_timerWaitForEndOfUpdateProcess.cancel();
			_timerWaitForEndOfUpdateProcess = new Timer();
			_task = new EndOfProcessTask();
			_timerWaitForEndOfUpdateProcess.schedule(_task, 5000);
			flush();
		}
	}

	class EndOfProcessTask extends TimerTask {

		public void run() {
			try {
				// println(new Date() +
				// " - Refreshing all bundles after update process....");
				if (!_fwkNeedRestart) {
					((ManagementImpl) _management).refresh(null, _out);
					((ManagementImpl) _management).startAllBundles(_out);
				}

			} catch (Throwable t) {
				println("Error EndOfProcess: " + t.toString());
			}
			flush();
		}

	}
}
