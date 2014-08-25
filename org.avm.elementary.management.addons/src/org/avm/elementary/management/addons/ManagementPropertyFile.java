package org.avm.elementary.management.addons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.avm.elementary.management.core.Management;
import org.avm.elementary.management.core.utils.Utils;

public class ManagementPropertyFile {
	private static final String PERSISTANCE_PROPERTY_FILE = System
			.getProperty(Management.AVMHOME_TAG)
			+ System.getProperty("file.separator")
			+ "bin"
			+ System.getProperty("file.separator") + "management.properties";

	String publicDownloadUrl;
	String publicUploadUrl;

	String privateDownloadUrl;
	String privateUploadUrl;

	String updateMode;
	
	boolean lastUpdateInPrivateZone=false;

	public boolean isLastUpdateInPrivateZone() {
		return lastUpdateInPrivateZone;
	}

	public void setLastUpdateInPrivateZone(boolean lastUpdateInPrivateZone) {
		this.lastUpdateInPrivateZone = lastUpdateInPrivateZone;
	}

	public String getUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(String updateMode) {
		this.updateMode = updateMode;
	}

	private long modifiedDate;

	private static ManagementPropertyFile _instance = null;

	private ManagementPropertyFile() {
		reload();

	}

	public static ManagementPropertyFile getInstance() {
		if (_instance == null) {
			_instance = new ManagementPropertyFile();
			_instance.reload();
		}
		return _instance;
	}

	private boolean propertyFileModified() {
		File file = new File(PERSISTANCE_PROPERTY_FILE);
		long modifyDate = file.lastModified();
		boolean modified = modifyDate != modifiedDate;
		if (modified) {
			modifiedDate = modifyDate;
		}
		System.out
				.println("[***ManagementConfiguration***] Management PropertyFile changed ? :"
						+ modified);
		return modified;
	}

	public boolean reload() {
		boolean modified = propertyFileModified();
		if (modified) {
			Properties p = Utils.loadProperties(PERSISTANCE_PROPERTY_FILE);

			publicDownloadUrl = p.getProperty(
					Management.PUBLIC_DOWNLOAD_URL_TAG,
					System.getProperty(Management.PUBLIC_DOWNLOAD_URL_TAG));
			publicUploadUrl = p.getProperty(Management.PUBLIC_UPLOAD_URL_TAG,
					System.getProperty(Management.PUBLIC_UPLOAD_URL_TAG));

			privateDownloadUrl = p.getProperty(
					Management.PRIVATE_DOWNLOAD_URL_TAG,
					System.getProperty(Management.PRIVATE_DOWNLOAD_URL_TAG));
			privateUploadUrl = p.getProperty(Management.PRIVATE_UPLOAD_URL_TAG,
					System.getProperty(Management.PRIVATE_UPLOAD_URL_TAG));
			
			updateMode = p.getProperty(Management.UPDATE_MODE_TAG,
					System.getProperty(Management.UPDATE_MODE_TAG));
			
			String  temp = p.getProperty(
					Management.LAST_UPDATE_IN_PRIVATE_ZONE_TAG,
					System.getProperty(Management.LAST_UPDATE_IN_PRIVATE_ZONE_TAG));
			if (temp != null && temp.endsWith("true")){
				lastUpdateInPrivateZone=true;
			}
			else{
				lastUpdateInPrivateZone=false;
			}
		}
		return modified;
	}

	public void save() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		
		if (updateMode != null)
			p.setProperty(Management.UPDATE_MODE_TAG, updateMode);
		if (publicDownloadUrl != null)
			p.setProperty(Management.PUBLIC_DOWNLOAD_URL_TAG, publicDownloadUrl);
		if (publicUploadUrl != null)
			p.setProperty(Management.PUBLIC_UPLOAD_URL_TAG, publicUploadUrl);
		if (privateDownloadUrl != null)
			p.setProperty(Management.PRIVATE_DOWNLOAD_URL_TAG,
					privateDownloadUrl);
		if (privateUploadUrl != null)
			p.setProperty(Management.PRIVATE_UPLOAD_URL_TAG, privateUploadUrl);
		
		p.setProperty(Management.LAST_UPDATE_IN_PRIVATE_ZONE_TAG, lastUpdateInPrivateZone?"true":"false");
		
		
		Utils.saveProperties(p, PERSISTANCE_PROPERTY_FILE);
	}

	public String getPublicDownloadUrl() {
		return publicDownloadUrl;
	}

	public void setPublicDownloadUrl(String publicDownloadUrl) {
		this.publicDownloadUrl = publicDownloadUrl;
	}

	public String getPublicUploadUrl() {
		return publicUploadUrl;
	}

	public void setPublicUploadUrl(String publicUploadUrl) {
		this.publicUploadUrl = publicUploadUrl;
	}

	public String getPrivateDownloadUrl() {
		return privateDownloadUrl;
	}

	public void setPrivateDownloadUrl(String privateDownloadUrl) {
		this.privateDownloadUrl = privateDownloadUrl;
	}

	public String getPrivateUploadUrl() {
		return privateUploadUrl;
	}

	public void setPrivateUploadUrl(String privateUploadUrl) {
		this.privateUploadUrl = privateUploadUrl;
	}
}
