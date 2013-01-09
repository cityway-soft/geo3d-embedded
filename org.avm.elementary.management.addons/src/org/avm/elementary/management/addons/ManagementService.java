package org.avm.elementary.management.addons;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.avm.elementary.management.core.Management;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * @author Didier LALLEMAND
 * 
 */
public interface ManagementService {
	public static final boolean DEBUG = Boolean.valueOf(
			System.getProperty("org.avm.debug", "false")).booleanValue();
	
	public static final String VEHICULE_PROPERTY=Management.VEHICULE_PROPERTY;
	
	public static final String EXPLOITATION_PROPERTY=Management.EXPLOITATION_PROPERTY;
	
	public static final String PLATEFORM_PROPERTY=Management.PLATEFORM_PROPERTY;

	public StartLevel getStartLevelService();

	public PackageAdmin getPackageAdminService();

	public void setDownloadURL(URL url) throws MalformedURLException;

	public void setUploadURL(URL url) throws MalformedURLException;

	public URL getDownloadURL();

	public URL getUploadURL();

	public void runScript(URL url);

	public void synchronize(PrintWriter out) throws Exception;

	public void shutdown(PrintWriter out, int waittime, int exitCode);

	public void send(String result);

}
