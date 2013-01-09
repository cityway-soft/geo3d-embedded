package org.avm.device.road.io;

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

public class DigitalOutputService extends ServiceTracker implements
		DigitalIODriver {

	private static final int CAPABILITY = 4;

	private Logger _log = Logger.getInstance(this.getClass());

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	private int _data;

	private int _handle;

	public DigitalOutputService(BundleContext context, ServiceReference device) {
		super(context, device, null);
		_listeners = new PropertyChangeSupport(this);
		_log.setPriority(Priority.DEBUG);
	}

	public int getCapability() {
		return CAPABILITY;
	}

	public boolean getValue(int index) {
		if (index >= CAPABILITY)
			throw new IndexOutOfBoundsException();

		int mask = 0;
		mask |= (1 << index);
		return ((_data & mask) == mask);
	}

	public void setValue(int index, boolean value) {
		if (index >= CAPABILITY)
			throw new IndexOutOfBoundsException();

		int data = 0, mask = 0;
		data = (value) ? (data |= (1 << (index))) : (data &= ~(1 << (index)));
		mask |= (1 << index);

		boolean old = ((_data & mask) == mask);

		_data = ROAD_IO.dio_write_ouput(_handle, data, mask);
		if (_log.isDebugEnabled()) {
			_log.debug("I/O[" + index + "] = " + ((_data & mask) == mask));
		}
		_listeners.fireIndexedPropertyChange(null, index, old, value);
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
		initialize();
		_sr = context.registerService(this.getClass().getName(), this,
				new Properties());
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		context.ungetService(reference);
		_sr.unregister();
		dispose();
		_device.setDriver(null);
		_device = null;
	}

	private void initialize() {
		_handle = ROAD_IO.dio_open("/dev/i2c-0");
		if (_handle < 0) {
			return;
		}
		_data = ROAD_IO.dio_write_ouput(_handle, 0x0000, 0xffff);
	}

	private void dispose() {
		if (_handle > 0) {
			ROAD_IO.dio_close(_handle);
			_handle = 0;
		}
	}

}
