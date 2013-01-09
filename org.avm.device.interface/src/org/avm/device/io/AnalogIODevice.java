package org.avm.device.io;

import org.apache.log4j.Logger;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.service.device.Device;

public class AnalogIODevice implements Device {

	public static int MATCH_SERIAL = 10;

	public static int MATCH_VERSION = 8;

	public static int MATCH_MODEL = 6;

	public static int MATCH_MAKE = 4;

	public static int MATCH_CLASS = 2;

	public static final String[] DEVICE_CATEGORY = { AnalogIODevice.class
			.getName() };

	public static final String DEVICE_DESCRIPTION = "Carte E/S analogique";

	private Logger _log = Logger.getInstance("org.avm.device.io");
	
	private AnalogIODriver _driver;

	private PropertyChangeSupport _listeners;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (_driver == null)
			return;
		_listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (_driver == null)
			return;
		_listeners.removePropertyChangeListener(listener);
	}

	public void notify(int index, boolean oldValue, boolean value) {
		_listeners.fireIndexedPropertyChange(null, index, oldValue, value);
	}

	public int getBitResolution() {
		if (_driver == null)
			return 0;
		return _driver.getBitResolution();
	}

	public long getValue(int index) {
		if (_driver == null)
			return 0;
		return _driver.getValue(index);
	}

	public void setValue(int index, int value) {
		if (_driver == null)
			return;
		_driver.setValue(index, value);
	}

	public void noDriverFound() {
		_log.info("Analog IO: No driver found");
	}

	public void setDriver(AnalogIODriver driver) {
		_driver = driver;
		_log.info("Analog IO: Driver found " + driver);
	}

}
