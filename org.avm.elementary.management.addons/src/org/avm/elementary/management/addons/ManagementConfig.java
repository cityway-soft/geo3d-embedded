package org.avm.elementary.management.addons;

public interface ManagementConfig {
	public String getPublicDownloadUrl();

	public String getPublicUploadUrl();

	public void setPublicDownloadUrl(String url);

	public void setPublicUploadUrl(String url);

	public String getPrivateDownloadUrl();

	public String getPrivateUploadUrl();

	public void setPrivateDownloadUrl(String url);

	public void setPrivateUploadUrl(String url);
}
