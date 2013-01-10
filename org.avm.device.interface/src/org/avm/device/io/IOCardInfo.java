package org.avm.device.io;

import java.util.Properties;

import org.osgi.service.device.Constants;

public class IOCardInfo extends Properties implements Constants {

	public static final String DEVICE_MANUFACTURER = "DEVICE_MANUFACTURER";

	public static final String DEVICE_MODEL = "DEVICE_MODEL";

	public static final String DEVICE_VERSION = "DEVICE_VERSION";

	public IOCardInfo(String category, String manufacturer, String model,
			String serial) {
		super();
		put(org.osgi.service.device.Constants.DEVICE_CATEGORY, category);
		put(DEVICE_MANUFACTURER, manufacturer);
		put(DEVICE_MODEL, model);
		put(DEVICE_SERIAL, serial);
	}

	public String getCategory() {
		return (String) get(org.osgi.service.device.Constants.DEVICE_CATEGORY);
	}

	public String getManufacturer() {
		return (String) get(DEVICE_MANUFACTURER);
	}

	public String getModel() {
		return (String) get(DEVICE_MODEL);
	}

	public String getSerial() {
		return (String) get(DEVICE_SERIAL);
	}

}
