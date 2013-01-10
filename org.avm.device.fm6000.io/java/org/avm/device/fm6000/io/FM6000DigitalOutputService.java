package org.avm.device.fm6000.io;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.fm6000.io.jni.COMVS_IO;
import org.avm.device.fm6000.io.jni.COMVS_IO_PIN;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class FM6000DigitalOutputService extends ServiceTracker implements
		DigitalIODriver {

	private static final int CAPABILITY = 8;

	private Logger _log = Logger.getInstance(this.getClass());

	private COMVS_IO_PIN[] _data;

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	public FM6000DigitalOutputService(BundleContext context,
			ServiceReference device) {
		super(context, device, null);
		_listeners = new PropertyChangeSupport(this);
	}

	public int getCapability() {
		return CAPABILITY;
	}

	public boolean getValue(int index) {
		throw new UnsupportedOperationException();
	}

	public void setValue(int index, boolean value) {
		if (index >= _data.length)
			throw new IndexOutOfBoundsException();
		boolean oldValue = (_data[index].getState() != 0) ? true : false;

		int handle = Activator.getDefault().getHandle();
		if (handle == 0) {
			return;
		}

		_data[index].setState(value ? 1 : 0);
		COMVS_IO.write(handle, _data, _data.length);

		if (_log.isDebugEnabled()) {
			_log.debug("I/O[" + index + "] = " + _data[index]);
		}
		_listeners.fireIndexedPropertyChange(null, index, oldValue, value);
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

	protected void initialize() {
		int n = getCapability();
		_data = new COMVS_IO_PIN[n];
		for (int i = 0; i < getCapability(); i++) {
			_data[i] = new COMVS_IO_PIN();
			_data[i].setId(COMVS_IO.IO_DIO_OUTPUT_0 + i);
			_data[i].setMode(COMVS_IO.IO_MODE_DIO_OUTPUT);
		}
		setValue(7, true);
	}

	protected void dispose() {
		int n = getCapability();
		if (_data != null) {
			for (int i = 0; i < getCapability(); i++) {
				_data[i] = null;
			}
			_data = null;
		}
	}

}
