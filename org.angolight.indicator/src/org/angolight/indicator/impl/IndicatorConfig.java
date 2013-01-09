package org.angolight.indicator.impl;

import java.util.Properties;

public interface IndicatorConfig {

	public final static String FILENAME_TAG = "filename";
	public final static String CAN_SERVICE_PID_TAG = "can-service-pid";

	public String getCanServicePid();

	public void setCanServicePid(String pid);

	public String getFilename();

	public void setFilename(String path);

	public Properties getProperties();

	public void setProperties(Properties p);

}
