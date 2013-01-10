package org.avm.elementary.directory.impl;

import java.util.Properties;

public interface DirectoryConfig {

	public boolean addProperty(String key, Properties properties);

	public void removeProperty(String key);

	public Properties getProperty(String key);

	public boolean loadProperties(String filename);

	public boolean saveProperties(String filename);

	public String getFileName();

	public void setFileName(String uri);

	public String getVersion();

	public void setVersion(String version);
	
	public void createDefaultProperties();
}
