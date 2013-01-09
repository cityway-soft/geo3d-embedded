package org.avm.elementary.management.core;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;


/**
 * @author Didier LALLEMAND
 * 
 */
public interface Management {

	public static final String AVMHOME_TAG = "org.avm.home";
	public static final String AVMDEBUG_TAG = "org.avm.debug";
	public static final String AVMVERSION_TAG = "org.avm.version";

	public static final String VEHICULE_PROPERTY="org.avm.vehicule.id";//deprecated
	public static final String EXPLOITATION_PROPERTY="org.avm.exploitation.id";//deprecated
	public static final String PLATEFORM_ID_PROPERTY="org.avm.plateform.id";//deprecated
	public static final String PLATEFORM_PROPERTY="org.avm.plateform";//deprecated
	
	public static final String TERMINAL_PLATEFORM_PROPERTY="org.avm.terminal.plateform";
	public static final String TERMINAL_ID_PROPERTY = "org.avm.terminal.id";
	public static final String TERMINAL_OWNER_PROPERTY = "org.avm.terminal.owner";
	public static final String TERMINAL_NAME_PROPERTY = "org.avm.terminal.name";



	
	String PRIVATE_DOWNLOAD_URL_TAG = "org.avm.download.private.url";
	String PRIVATE_UPLOAD_URL_TAG = "org.avm.upload.private.url";
	String PUBLIC_DOWNLOAD_URL_TAG = "org.avm.download.public.url";
	String PUBLIC_UPLOAD_URL_TAG = "org.avm.upload.public.url";
	String DEFAULT_DOWNLOAD_URL_TAG = "org.avm.download.default.url";
	String DEFAULT_UPLOAD_URL_TAG = "org.avm.upload.default.url";
	
	public static boolean DEBUG=  Boolean.valueOf(System.getProperty(AVMDEBUG_TAG, "false"))
	.booleanValue();

	
	
	public static final int EXITCODE_RESTART_APPLICATION = 1;
	
	public static final int EXITCODE_STOP_BY_WATCHDOG = 2;
	
	public static final int EXITCODE_REBOOT_PLATEFORM = 3;
	
	public static final int TIME_TO_FWK_SHUTDOWN = 0;

	public void synchronize(PrintWriter out) throws Exception;
		
	public void shutdown(PrintWriter out,int waittime, int exitCode);
	
	public void setDownloadURL(URL url) throws MalformedURLException;
	
	public void setUploadURL(URL url) throws MalformedURLException;

	public URL getUploadURL();

	public URL getDownloadURL();
	
	public StartLevel getStartLevelService();

	public PackageAdmin getPackageAdminService();


}
