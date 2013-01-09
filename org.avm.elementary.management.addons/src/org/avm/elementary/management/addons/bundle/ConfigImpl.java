package org.avm.elementary.management.addons.bundle;

import java.util.Dictionary;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.management.addons.ManagementConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements ManagementConfig {

	public static final String PUBLIC_DOWNLOAD_TAG = "org.avm.elementary.management.public.download.url";

	public static final String DEFAULT_PUBLIC_DOWNLOAD = System.getProperty(
			PUBLIC_DOWNLOAD_TAG, "ftp://avm:avm++@10.1.2.160");

	public static final String PUBLIC_UPLOAD_TAG = "org.avm.elementary.management.public.upload.url";

	public static final String DEFAULT_PUBLIC_UPLOAD = new String(
			"ftp://wifi.ftpsite");

	public static final String PRIVATE_DOWNLOAD_TAG = "org.avm.elementary.management.private.download";

	public static final String DEFAULT_PRIVATE_DOWNLOAD = new String(
			"ftp://wifi.ftpsite");

	public static final String PRIVATE_UPLOAD_TAG = "org.avm.elementary.management.private.upload";

	public static final String DEFAULT_PRIVATE_UPLOAD = new String(
			"ftp://wifi.ftpsite");

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		// result.put(PUBLIC_DOWNLOAD_TAG, DEFAULT_PUBLIC_DOWNLOAD);
		// result.put(PUBLIC_UPLOAD_TAG, DEFAULT_PUBLIC_UPLOAD);
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public String getPublicDownloadUrl() {
		return (String) _config.get(PUBLIC_DOWNLOAD_TAG);
	}

	public void setPublicDownloadUrl(String value) {
		_config.put(PUBLIC_DOWNLOAD_TAG, value);
	}

	public void setPublicUploadUrl(String url) {
		_config.put(PUBLIC_UPLOAD_TAG, url);
	}

	public String getPublicUploadUrl() {
		return (String) _config.get(PUBLIC_UPLOAD_TAG);
	}

	public String getPrivateDownloadUrl() {
		return (String) _config.get(PRIVATE_DOWNLOAD_TAG);
	}

	public void setPrivateDownloadUrl(String value) {
		_config.put(PRIVATE_DOWNLOAD_TAG, value);
	}

	public void setPrivateUploadUrl(String url) {
		_config.put(PRIVATE_UPLOAD_TAG, url);
	}

	public String getPrivateUploadUrl() {
		return (String) _config.get(PRIVATE_UPLOAD_TAG);
	}

}
