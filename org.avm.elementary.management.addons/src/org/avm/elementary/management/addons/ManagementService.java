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
	
	public StartLevel getStartLevelService();

	public PackageAdmin getPackageAdminService();

	public void setDownloadURL(URL url) throws MalformedURLException, Exception;

	public void setUploadURL(URL url) throws MalformedURLException, Exception;
	
	public void setPublicMode() throws MalformedURLException, Exception;

	public void setPrivateMode() throws MalformedURLException, Exception;

	public URL getDownloadURL() throws Exception;

	public URL getUploadURL() throws Exception;

	public void runScript(URL url);

	public void synchronize(PrintWriter out) throws Exception;

	public void shutdown(PrintWriter out, int waittime, int exitCode) throws Exception;

	public void send(String result);
	
	public boolean isWLANMode();
	
	public void setWLANMode(boolean b)throws MalformedURLException;
	
	
	

}
