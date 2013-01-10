package org.avm.elementary.management.core.bundle;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.avm.elementary.management.core.Management;
import org.avm.elementary.management.core.ManagementImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

public class Activator implements BundleActivator, Management {

	static final String PID = Management.class.getName();

	private static final String LOGTAG = "DEBUG [" + Activator.class.getName()
			+ "] ";

	private ManagementImpl _peer;

	private ServiceRegistration _sr;

	private boolean DEBUG;

	public Activator() {
		
	}


	public StartLevel getStartLevelService() {
		return _peer.getStartLevelService();
	}



	public void start(BundleContext context) throws Exception {
		DEBUG = Boolean.valueOf(System.getProperty("org.avm.debug", "false")).booleanValue();
		_peer = new ManagementImpl();
		debug("activating.......");
		_peer.setBundleContext(context);
		debug("starting...");
		_peer.start();
		// Dictionary dic = new Properties();
		// dic.put("service.pid", ManagementService.class.getName());
		_sr = context.registerService(Management.class.getName(), _peer,
				null);
		debug(_sr + " registered.");
		debug("activated.");
	}

	public void stop(BundleContext context) throws Exception {
		debug("deactivating...");
		_sr.unregister();
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


	public void synchronize(PrintWriter p) throws Exception {
		_peer.synchronize(p);
	}

	public URL getDownloadURL() {
		return  _peer.getDownloadURL();
	}

	public URL getUploadURL() {
		return _peer.getUploadURL();
	}

	public void setDownloadURL(URL url) throws MalformedURLException {
		_peer.setDownloadURL(url);
	}

	public void setUploadURL(URL url) throws MalformedURLException {
		_peer.setUploadURL(url);
	}

}
