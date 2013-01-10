package org.avm.hmi.swt.management;

import java.util.Properties;

public interface ManagementConfig {

	public void addProperty(String key, Properties properties);

	public void removeProperty(String key);

	public Properties getProperty(String key);
}
