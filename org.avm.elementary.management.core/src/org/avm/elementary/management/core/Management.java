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

	String PRIVATE_DOWNLOAD_URL_TAG = "org.avm.download.private.url";
	String PRIVATE_UPLOAD_URL_TAG = "org.avm.upload.private.url";
	//
	String PUBLIC_DOWNLOAD_URL_TAG = "org.avm.download.public.url";
	String PUBLIC_UPLOAD_URL_TAG = "org.avm.upload.public.url";
	//

	String DEFAULT_PRIVATE_UPLOAD_URL = "http://avm:avm++@ftpserver.avm.org/terminal/$i/upload";
	String DEFAULT_PUBLIC_UPLOAD_URL = "http://avm:avm++@saml.avm.org/terminal/$i/upload";

	String DEFAULT_PRIVATE_DOWNLOAD_URL = "http://avm:avm++@ftpserver.avm.org/terminal/$i/$p/$o/$n/download";
	String DEFAULT_PUBLIC_DOWNLOAD_URL = "http://avm:avm++@saml.avm.org/terminal/$i/$p/$o/$n/download";

	String UPDATE_MODE_TAG = "org.avm.default.upload-download.mode";
	String LAST_UPDATE_IN_PRIVATE_ZONE_TAG = "org.avm.last-update-in-private-zone";

	String MODE_PUBLIC_TAG = "public";

	String MODE_PRIVATE_TAG = "private";

	String MODE_BYUSER_TAG = "by-user";

	int MODE_DEFAULT = -1;

	int MODE_PUBLIC = 0;

	int MODE_PRIVATE = 1;

	int MODE_BYUSER = 2;

	int MODE_LASTUSED = 3;
	
	public static boolean DEBUG = Boolean.valueOf(
			System.getProperty(AVMDEBUG_TAG, "false")).booleanValue();

	public static final int EXITCODE_RESTART_APPLICATION = 1;

	public static final int EXITCODE_STOP_BY_WATCHDOG = 2;

	public static final int EXITCODE_REBOOT_PLATEFORM = 3;

	public static final int TIME_TO_FWK_SHUTDOWN = 0;

	public static final String DEPLOYED = "management-core.downloaded"; 

	public void synchronize(PrintWriter out, boolean force) throws Exception;

	public void synchronize(PrintWriter out) throws Exception;

	public void sendBundleList(int mode) throws Exception;

	public int getCurrentMode();

	public void shutdown(PrintWriter out, int waittime, int exitCode);

	public void setDownloadUrl(URL url) throws MalformedURLException;

	public void setUploadUrl(URL url) throws MalformedURLException;

	public void setDownloadUrl(int mode) throws MalformedURLException;

	public void setUploadUrl(int mode) throws MalformedURLException;

	public String getUploadUrl(int mode);

	public String getDownloadUrl(int mode);

	public URL getCurrentUploadUrl();

	public URL getCurrentDownloadUrl();

	public StartLevel getStartLevelService();

	public PackageAdmin getPackageAdminService();

	public void setCurrentMode(int mode) throws MalformedURLException;

}
