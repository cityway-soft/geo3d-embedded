package org.avm.elementary.common;

import java.util.Properties;

public interface DeviceConfig {

	public static final String DEVICE_DESCRIPTION = "DEVICE_DESCRIPTION";

	public static final String DEVICE_MANUFACTURER = "DEVICE_MANUFACTURER";

	public static final String DEVICE_VERSION = "DEVICE_VERSION";

	public static final String DEVICE_NAME = "DEVICE_NAME";

	public static final String DEVICE_CATEGORY = "DEVICE_CATEGORY";

	public static final String DEVICE_MODEL = "DEVICE_MODEL";

	public static final String DEVICE_SERIAL = "DEVICE_SERIAL";

	public String getCategory();

	public void setCategory(String category);

	public String getManufacturer();

	public void setManufacturer(String manufacturer);

	public void setDescription(String description);

	public String getDescription();

	public void setVersion(String version);

	public String getVersion();

	public void setName(String name);

	public String getName();

	public String getModel();

	public void setModel(String model);

	public String getSerial();

	public void setSerial(String serial);

	public String getParamerter(String name);

	public void setParameters(String name, String value);

	public Properties getParameters();

	public void putParameters(Properties p);
}
