package org.avm.elementary.management.core;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.avm.elementary.management.core.utils.Utils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.service.startlevel.StartLevel;

class SynchronizeBundlesCommand implements BundleListener {

	private BundleContext _context;

	private Management _management;

	private static PrintWriter _out = null;

	private Timer _timerWaitForEndOfUpdateProcess;

	private EndOfProcessTask _task;

	private boolean _fwkNeedRestart;

	public SynchronizeBundlesCommand(BundleContext context,
			Management management, PrintWriter out) {
		_context = context;
		_management = management;

		_out = out;
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
			println("*     * A U T O M A T I C * U P D A T E *         *");
			println("***************************************************");
			flush();
			deploy();
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			println("!!!!!!!!!!!! => Erreur au cours du deploiement :");
			e.printStackTrace(_out);
			println("==============================================================");
		} finally {

		}
	}

	private void downloadFile(String filename, String destdir)
			throws IOException {

		File dest = new File(destdir + "/" + filename);
		if (dest.exists()) {
			dest.delete();
		}

		OutputStream os = new BufferedOutputStream(new FileOutputStream(
				dest.getAbsoluteFile()));

		InputStream is;

		URLConnection connection = getRemoteFileURLConnection(filename);
		if (connection != null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				System.out.println("writeFTPReport sleep interrupted !");
			}

			is = connection.getInputStream();

			BufferedOutputStream fileOut = new BufferedOutputStream(os);

			int c;
			while ((c = is.read()) > -1) {
				fileOut.write(c);
			}

			fileOut.flush();
			fileOut.close();

			println("Management bundle downloaded.");
		} else {
			println("Unable to download management bundle !");
		}

	}

	private boolean downloadManagementBundle() {
		boolean status = false;
		String bundlefilename = this.getClass().getPackage().getName() + ".jar";
		println("/!\\ Downloading management bundle");
		try {
			String outdir = System.getProperty("java.io.tmpdir");
			downloadFile(bundlefilename + ".md5", outdir);
			downloadFile(bundlefilename, outdir);

			File md5file = new File(outdir + "/" + bundlefilename + ".md5");
			File jarfile = new File(outdir + "/" + bundlefilename);

			if (checkFile(md5file)) {
				if (md5file.length() != 0 && jarfile.length() != 0) {
					String destdir = System.getProperty("org.avm.home")
							+ "/lib";

					File current_md5file = new File(destdir + "/"
							+ md5file.getName());
					File current_jarfile = new File(destdir + "/"
							+ jarfile.getName());

					if (current_md5file.exists()) {
						current_md5file.delete();
					}

					if (current_jarfile.exists()) {
						current_jarfile.delete();
					}

					move(md5file, current_md5file);
					move(jarfile, current_jarfile);
					status = true;

				} else {
					println("Error: jar or md5 file empty");
				}
			} else {
				println("Error: md5 file does't match");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			println("Error:" + t.getMessage());
		}

		return status;
	}

	private void move(File afile, File bfile) throws IOException {
		InputStream inStream = null;
		OutputStream outStream = null;

		inStream = new FileInputStream(afile);
		outStream = new FileOutputStream(bfile);

		byte[] buffer = new byte[1024];

		int length;
		// copy the file content in bytes
		while ((length = inStream.read(buffer)) > 0) {

			outStream.write(buffer, 0, length);

		}

		inStream.close();
		outStream.close();

		// delete the original file
		afile.delete();

	}

	public static boolean checkFile(File md5file) {
		boolean result = false;

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					md5file.getAbsolutePath()));
			String line = br.readLine();
			int idx = line.indexOf(" ");
			String md5 = line.substring(0, idx).trim();
			String filename = line.substring(idx + 1).trim();

			String genmd5 = Utils.genMD5(new File(md5file.getParentFile()
					.getAbsoluteFile() + "/" + filename));

			result = (genmd5 != null && md5.equals(genmd5));
			println("genmd5=" + genmd5 + ", md5=" + md5 + "(check=" + result
					+ ")");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void stopBundlesToUpdate(BundleList list) {
		Enumeration e = list.elements();
		while (e.hasMoreElements()) {
			BundleProperties bp = (BundleProperties) e.nextElement();
			if (bp.getStartlevel() > 0) {
				String bundleName = bp.getSymbolicName();
				Bundle bundle = getBundle(bundleName);

				boolean isManagementBundle = this.getClass().getPackage()
						.getName().indexOf(bundleName) != -1;

				if (isManagementBundle) {
					// -- attention : ne pas stopper le bundle management (sinon
					// redemarrage manuel!)
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

	public void deploy() throws IOException {
		BundleList bundleList;
		try {
			bundleList = downloadBundleList();

			if (bundleList == null) {
				String url1 = getBundleListURL(BundleList.FILENAME, false);
				println("Failed to get bundles list : " + url1);
				String url2 = getBundleListURL(BundleList.FILENAME, true);
				println("Failed to get default bundles list : " + url2);
				throw new IOException("Cannot get bundle.list from " + url1
						+ " or " + url2);
			}

			File managementDeployedFile = new File(
					System.getProperty("org.avm.home") + "/lib/"
							+ Management.DEPLOYED);
			boolean forceSendBundleList = managementDeployedFile.exists();

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
			boolean changed = false;
			if (bundleList.size() > 0) {
				Enumeration e = bundleList.elements();

				while (e.hasMoreElements()) {
					updated = false;
					BundleProperties bundleProperties = (BundleProperties) e
							.nextElement();
					int bundleStartlevel = bundleProperties.getStartlevel();

					String bName = bundleProperties.getSymbolicName();

					Bundle bundle = getBundle(bName);

					try {
						if (bundleStartlevel < 0) {
							if (bundle != null) {
								changed = true;
								bundle.uninstall();
							}
							continue;
						} else if (bundleStartlevel > 0) {
							if (bundle == null) {
								String msg = "[update]" + bName + " INSTALL";
								println(msg);
								flush();
								install(bundleProperties);
								changed = true;
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

								if (_management.getClass().getPackage()
										.getName().indexOf(bName) != -1) {
									// --ignore management.core himself !

									String versionToDownload = bundleProperties
											.getVersion().trim();
									String versionAlreadyDownloaded = "";
									if (managementDeployedFile.exists()) {
										FileInputStream in = new FileInputStream(
												managementDeployedFile);
										byte[] buf = new byte[1024];
										in.read(buf);
										versionAlreadyDownloaded = new String(
												buf).trim();
									}
									println("Management version to download         : '"
											+ versionToDownload + "'");
									println("Management version already downloaded  : '"
											+ versionAlreadyDownloaded + "'");
									if (versionToDownload
											.equals(versionAlreadyDownloaded) == false) {
										if (downloadManagementBundle()) {
											// -- creation fichier temoin pour
											// forcer l'envoi du rapport de mise
											// à jour au prochain démarrage
											FileOutputStream out = new FileOutputStream(
													managementDeployedFile);
											out.write(versionToDownload
													.getBytes());
											out.close();
										}
									}
									else{
										println("Management version already downloaded : '"
												+ versionAlreadyDownloaded + "' !");
									}
									forceSendBundleList = false;
									continue;
								}

								update(bundleProperties);
								changed = true;
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

			if (changed || forceSendBundleList) {
				try {
					_management.sendBundleList(_management.getCurrentMode());
					if (forceSendBundleList) {
						managementDeployedFile.delete();
					}
				} catch (Exception e) {
					println("ERR unable to send bundle list report");
				}
			}

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
		} catch (UpdateNotAllowedException e1) {
			println(Constants.SETCOLOR_FAILURE
					+ "/!\\ UPDATE NOT ALLOWED BY SERVER"
					+ Constants.SETCOLOR_NORMAL);
			return;
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

	private void update(BundleProperties bundleProperties) throws IOException,
			BundleException {
		String path = bundleProperties.getPath();
		URLConnection connection = getRemoteBundleURLConnection(path);
		println("Download " + connection.getURL());
		Bundle bundle = getBundle(bundleProperties.getSymbolicName());
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
	private void install(BundleProperties bundleProperties) throws IOException,
			BundleException {
		String path = bundleProperties.getPath();
		URLConnection connection = getRemoteBundleURLConnection(path);
		if (connection != null) {
			println("Downloading " + connection.getURL());
			Bundle bundle = _context.installBundle(connection.getURL()
					.toString(), connection.getInputStream());
			setBundleStartLevel(bundle, bundleProperties.getStartlevel());
		} else {
			println("Error cannot download bundle " + path + "  !!!!");
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
	 * retourne une hashtable contenant la liste des bundles sur le repository
	 * 
	 * @return
	 * @throws IOException
	 * @throws IOException
	 * @throws IOException
	 * @throws UpdateNotAllowedException
	 */
	private BundleList downloadBundleList() throws IOException,
			UpdateNotAllowedException {
		BundleList bundleList = null;
		bundleList = getBundleList(BundleList.FILENAME
				+ BundleList.COMPRESSED_EXT);
		if (bundleList == null) {
			bundleList = getBundleList(BundleList.FILENAME);
		}
		return bundleList;
	}

	private String getBundleListURL(String filename, boolean useDefault) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(_management.getCurrentDownloadUrl() + "/" + filename);
		if (_management.getCurrentDownloadUrl().getProtocol()
				.startsWith("http")) {
			buffer.append("?mode=" + _management.getCurrentMode());
		}

		String strurl = Utils.formatURL(buffer.toString(), useDefault);
		return strurl;
	}

	private BundleList getBundleList(String filename)
			throws UpdateNotAllowedException {

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

		if (bundleList != null) {
			println("Bundle-List downloaded from : " + strurl);
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

				boolean check;

				String packtoInstall = bundleProperties.getPack();
				String packInstalled = (String) bundles[i].getHeaders().get(
						"TAB-Pack");
				// -- on retire les bundles dont les jeux sont identiques
				check = (packtoInstall == null && packInstalled == null);
				check = check
						|| (packtoInstall != null && packInstalled != null && packInstalled
								.equals(packtoInstall));
				if (check) {
					// -- si les pack ne sont pas renseignés ou si ils sont
					// égaux on controle la version
					// System.out.println(packInstalled + " == " + packtoInstall
					// + " ==> pack ok, now check version");

					String bundleListVersion = bundleProperties.getVersion();
					//double bundleVersion = Utils.getVersion(bundleListVersion);
					
					
					String currentBundleVersion = (String) bundles[i].getHeaders()
							.get("Bundle-Version");
					//double currentVersion = Utils.getVersion(currentBundleVersion);
					
					
					int result = Utils.compareVersion(bundleListVersion, currentBundleVersion);
					// -- on retire les bundles dont la version est plus recente
					// que
					// celle du bundles.list
					check = ((result==0 || result<-1) && bundleProperties
							.getStartlevel() >= 0);

					if (check) {
						bundleList.remove(bundleName);
					}
				}

			}
			flush();

			Enumeration e = bundleList.elements();
			while (e.hasMoreElements()) {
				BundleProperties bl = (BundleProperties) e.nextElement();
				String bundlename = bl.getSymbolicName();
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

				// if (bundleProperties.isEnable() == false) {
				// newStartLevel = 999; // disabled
				// }

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

	private BundleList loadFromURL(String strurl) throws IOException,
			UpdateNotAllowedException {
		URL url = new URL(strurl);
		BundleList bundleList = BundleList.load(url);
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

	private URLConnection getURLConnection(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		String userinfo = url.getUserInfo();
		if (userinfo != null && url.getProtocol().equals("http")) {
			int idx = userinfo.indexOf(":");
			String password = userinfo.substring(idx + 1);
			String user = userinfo.substring(0, idx);
			String encoded = new String(
					Base64.encodeBase64((user + ":" + password).getBytes()));
			connection.setRequestProperty("Authorization", "Basic " + encoded);
		}
		check(connection);
		return connection;
	}

	private URLConnection getRemoteFileURLConnection(String filename)
			throws MalformedURLException {
		String downloadURL = _management.getCurrentDownloadUrl().toString();

		StringBuffer buf = new StringBuffer();
		buf.append(downloadURL);
		if (downloadURL.endsWith("/") == false
				&& filename.startsWith("/") == false) {
			buf.append("/");
		}
		buf.append(filename);

		// println("downloadURL : " + downloadURL);
		// println("Filename : " + filename);
		// println("buf : " + buf.toString());

		String surl = Utils.formatURL(buf.toString(), false);
		URL url = new URL(surl);
		URLConnection connection = null;
		String failure = "";
		try {
			connection = getURLConnection(url);
			return connection;
		} catch (IOException e) {
			failure = e.getMessage();
		}

		surl = Utils.formatURL(buf.toString(), true);
		url = new URL(surl);
		try {
			connection = getURLConnection(url);
			return connection;
		} catch (IOException e) {
			println("Error 1 : " + failure);
			println("Error 2 : " + e.getMessage());
		}
		return null;
	}

	private URLConnection getRemoteBundleURLConnection(String bundlePath)
			throws MalformedURLException {
		return getRemoteFileURLConnection(bundlePath);
	}

	private void check(URLConnection c) throws IOException {
		c.getInputStream();
	}

	// private String bundleState2String(int state) {
	// switch (state) {
	// case BundleEvent.INSTALLED:
	// return "INSTALLED";
	// case BundleEvent.RESOLVED:
	// return "RESOLVED";
	// case BundleEvent.STARTED:
	// return "STARTED";
	// case BundleEvent.STARTING:
	// return "STARTING";
	// case BundleEvent.STOPPED:
	// return "STOPPED";
	// case BundleEvent.STOPPING:
	// return "STOPPING";
	// case BundleEvent.UNINSTALLED:
	// return "UNINSTALLED";
	// case BundleEvent.UNRESOLVED:
	// return "UNRESOLVED";
	// case BundleEvent.UPDATED:
	// return "UPDATED";
	//
	// default:
	// return "unknown. ";
	// }
	// }

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
