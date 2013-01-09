package org.avm.device.road.io;

import java.io.IOException;
import java.util.BitSet;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;
import org.avm.device.road.io.jni.ROAD_IO;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class AudioAmplifierOutputService extends ServiceTracker implements
		DigitalIODriver {

	private static final int CAPABILITY = 2;

	private Logger _log = Logger.getInstance(this.getClass());

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	private BitSet _data;

	private int _handle;

	public AudioAmplifierOutputService(BundleContext context,
			ServiceReference device) {
		super(context, device, null);
		_data = new BitSet();
		_listeners = new PropertyChangeSupport(this);
		_log.setPriority(Priority.DEBUG);
	}

	public int getCapability() {
		return CAPABILITY;
	}

	public boolean getValue(int index) {
		if (index >= CAPABILITY)
			throw new IndexOutOfBoundsException();
		return _data.get(index);
	}

	private boolean write(int index, boolean value) {
		try {
			StringBuffer buffer = new StringBuffer("i2conf 6c w 81 ");
			buffer.append((index == 0) ? "04 " : "02 ");
			buffer.append((value) ? "10" : "00");
			Process process = Runtime.getRuntime().exec(buffer.toString());
			return (process.waitFor() == 0);
		} catch (Exception e) {
			return false;
		}
	}

	public void setValue(int index, boolean value) {
		if (index >= CAPABILITY)
			throw new IndexOutOfBoundsException();

		if (write(index, value)) {
			boolean old = _data.get(index);
			if (value) {
				_data.set(index);
			} else {
				_data.clear(index);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("I/O[" + index + "] = " + value);
			}
			_listeners.fireIndexedPropertyChange(null, index, old, value);
		} else {
			_log.error("[DSU] echec write I/O");
		}

	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}

	public Object addingService(ServiceReference reference) {
		_device = (DigitalIODevice) context.getService(reference);
		_device.setDriver(this);
		_sr = context.registerService(this.getClass().getName(), this,
				new Properties());
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		context.ungetService(reference);
		_sr.unregister();
		_device.setDriver(null);
		_device = null;
	}

}
