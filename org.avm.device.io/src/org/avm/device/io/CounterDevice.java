package org.avm.device.io;

import org.apache.log4j.Logger;
import org.osgi.service.device.Device;

public class CounterDevice implements Device {

	public static int MATCH_SERIAL = 10;

	public static int MATCH_VERSION = 8;

	public static int MATCH_MODEL = 6;

	public static int MATCH_MAKE = 4;

	public static int MATCH_CLASS = 2;

	public static final String[] DEVICE_CATEGORY = { CounterDevice.class
			.getName() };

	public static final String DEVICE_DESCRIPTION = "Carte Compteur";

	private Logger _log = Logger.getInstance("org.avm.device.io");

	private CounterDriver _driver;

	public int getBitResolution() {
		if (_driver == null)
			return 0;
		return _driver.getBitResolution();
	}

	public int getValue(int index) {
		if (_driver == null)
			return 0;
		return _driver.getValue(index);
	}

	public void setValue(int index, int value) {
		if (_driver == null)
			return;
		_driver.reset(index);
	}

	public void noDriverFound() {
		_log.info("Counter: No driver found");
	}

	public void setDriver(CounterDriver driver) {
		_driver = driver;
		_log.info("Counter: Driver found " + driver);
	}

}
