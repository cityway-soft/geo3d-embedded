package org.avm.device.io;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.IndexedPropertyChangeEvent;
import org.avm.elementary.common.PropertyChangeEvent;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.avm.elementary.log4j.Activator;
import org.osgi.service.device.Device;

public class DigitalIODevice implements Device, PropertyChangeListener {

	public static int MATCH_SERIAL = 10;

	public static int MATCH_VERSION = 8;

	public static int MATCH_MODEL = 6;

	public static int MATCH_MAKE = 4;

	public static int MATCH_CLASS = 2;

	public static final String[] DEVICE_CATEGORY = { DigitalIODevice.class
			.getName() };

	public static final String DEVICE_DESCRIPTION = "Carte E/S digitale";

	private Logger _log = Logger.getInstance("org.avm.device.io");

	private DigitalIODriver _driver;

	private PropertyChangeSupport _listeners;

	private MessageFormat _form = new MessageFormat("index_{0,number}");

	public DigitalIODevice() {
		super();
		_listeners = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(int index,
			PropertyChangeListener listener) {
		Object[] o = { new Integer(index) };
		_listeners.addPropertyChangeListener(_form.format(o), listener);
	}

	public void removePropertyChangeListener(int index,
			PropertyChangeListener listener) {
		Object[] o = { new Integer(index) };
		_listeners.removePropertyChangeListener(_form.format(o), listener);
	}

	public boolean getValue(int index) {
		if (_driver == null)
			throw new NullPointerException();
		return _driver.getValue(index);
	}

	public void setValue(int index, boolean value) {
		if (_driver == null)
			throw new NullPointerException();
		_driver.setValue(index, value);
	}

	public void noDriverFound() {
		_log.info("Digital IO: No driver found");
	}

	public void setDriver(DigitalIODriver driver) {
		if (driver != null) {
			_driver = (DigitalIODriver) driver;			
			_driver.addPropertyChangeListener(this);
		} else {
			if (_driver != null)
				_driver.removePropertyChangeListener(this);
			_driver = null;
		}

		_log.info("Digital IO: Driver found " + driver);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		IndexedPropertyChangeEvent event = (IndexedPropertyChangeEvent) evt;
		Object[] o = { new Integer(event.getIndex()) };
		_log.info("notify I/O[" + event.getIndex() + "] = "
				+ event.getNewValue());
		_listeners.fireIndexedPropertyChange(_form.format(o), event.getIndex(),
				event.getOldValue(), event.getNewValue());

	}

}
