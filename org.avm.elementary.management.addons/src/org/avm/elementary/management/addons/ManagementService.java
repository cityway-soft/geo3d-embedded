package org.avm.elementary.management.addons;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

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

	public void setDownloadUrl(URL url) throws MalformedURLException, Exception;

	public void setUploadUrl(URL url) throws MalformedURLException, Exception;
	
	public void setPublicMode() throws MalformedURLException, Exception;

	public void setPrivateMode() throws MalformedURLException, Exception;

	public URL getDownloadUrl(int mode) throws Exception;

	public URL getUploadUrl(int mode) throws Exception;

	public void runScript(URL url);

	public void synchronize(PrintWriter out, int mode) throws Exception;

	public void shutdown(PrintWriter out, int waittime, int exitCode) throws Exception;

	public void send(String result);
	
	public void setPrivateMode(boolean b)throws MalformedURLException;

	public URL getCurrentDownloadUrl()throws Exception;

	public URL getCurrentUploadUrl()throws Exception;
	
	public int getCurrentMode() throws Exception;
	
	
	

}
