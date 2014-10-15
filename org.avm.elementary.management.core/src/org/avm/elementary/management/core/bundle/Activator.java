package org.avm.elementary.management.core.bundle;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.avm.elementary.management.core.Management;
import org.avm.elementary.management.core.ManagementImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

public class Activator implements BundleActivator, Management {

	static final String PID = Management.class.getName();

	private static final String LOGTAG = "DEBUG [" + Activator.class.getName()
			+ "] ";

	private ManagementImpl _peer;

	private boolean DEBUG;

	public Activator() {

	}

	public StartLevel getStartLevelService() {
		return _peer.getStartLevelService();
	}

	public void start(BundleContext context) throws Exception {
		DEBUG = Boolean.valueOf(System.getProperty("org.avm.debug", "false"))
				.booleanValue();
		_peer = new ManagementImpl();
		debug("activating.......");
		_peer.setBundleContext(context);

		_peer.start();
		debug("activated.");
	}

	public void stop(BundleContext context) throws Exception {
		_peer.stop();
		_peer.setBundleContext(context);
		debug("deactivated.");
	}

	private void debug(String debug) {
		if (DEBUG) {
			System.out.println(LOGTAG + debug);
		}
	}

	public PackageAdmin getPackageAdminService() {
		return _peer.getPackageAdminService();
	}

	public void shutdown(PrintWriter out, int timeout, int exitcode) {
		_peer.shutdown(out, timeout, exitcode);
	}

	public void synchronize(PrintWriter p, boolean force) throws Exception {
		_peer.synchronize(p, force);
	}
	
	public void synchronize(PrintWriter p) throws Exception {
		_peer.synchronize(p);
	}

	public URL getCurrentDownloadUrl() {
		return _peer.getCurrentDownloadUrl();
	}

	public URL getCurrentUploadUrl() {
		return _peer.getCurrentUploadUrl();
	}

	public void setDownloadUrl(URL url) throws MalformedURLException {
		_peer.setDownloadUrl(url);
	}

	public void setUploadUrl(URL url) throws MalformedURLException {
		_peer.setUploadUrl(url);
	}
	
	public void setDownloadUrl(int mode) throws MalformedURLException {
		_peer.setDownloadUrl(mode);
	}

	public void setUploadUrl(int mode) throws MalformedURLException {
		_peer.setUploadUrl(mode);
	}
	
	
	public String getDownloadUrl(int mode)  {
		return _peer.getDownloadUrl(mode);
	}

	public String getUploadUrl(int mode)  {
		return _peer.getUploadUrl(mode);
	}


	public void sendBundleList(int mode) throws Exception {
		_peer.sendBundleList(mode);
	}

	public void setCurrentMode(int mode) throws MalformedURLException {
		_peer.setCurrentMode(mode);
	}



	public int getCurrentMode() {
		return _peer.getCurrentMode();
	}

}
