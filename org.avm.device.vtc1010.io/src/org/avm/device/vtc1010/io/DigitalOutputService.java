package org.avm.device.vtc1010.io;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;
import org.avm.device.vtc1010.common.api.Vtc1010IO;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class DigitalOutputService extends ServiceTracker implements
		DigitalIODriver {

	private static final int CAPABILITY = 3;

	private Logger _log = Logger.getInstance(this.getClass().getName());

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	private int handle = 0;

	private Vtc1010IO ioAccess = null;

	public Vtc1010IO getIoAccess() {
		return ioAccess;
	}

	public void setIoAccess(Vtc1010IO ioAccess) {
		this.ioAccess = ioAccess;
	}

	public DigitalOutputService(BundleContext context, ServiceReference device) {
		super(context, device, null);
		// _log.setPriority(Priority.DEBUG);
		_listeners = new PropertyChangeSupport(this);
	}

	public int getCapability() {
		return CAPABILITY;
	}

	public boolean getValue(int index) {
		throw new UnsupportedOperationException();
	}

	public void setValue(int index, boolean value) {
		if (index >= CAPABILITY) {
			throw new IndexOutOfBoundsException();
		}
		if (ioAccess != null) {
			int dout = ioAccess.read(handle, Vtc1010IO.NON_BLOCKING_READ);
			boolean oldValue = get(dout, index);
			if (value) {
				dout = set(dout, index);
			} else {
				dout = clear(dout, index);
			}
			ioAccess.write(handle, dout);
			if (_log.isDebugEnabled()) {
				_log.debug("Write 0x" + Integer.toHexString(dout));
			}

			_listeners.fireIndexedPropertyChange(null, index, oldValue, value);
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
		if (ioAccess != null) {
			handle = ioAccess.open();
		}

	}

	private void dispose() {
		if (ioAccess != null) {
			ioAccess.close(handle);
		}

	}

	private static final int set(int flag, int bit) {
		int mask = 1 << bit;
		return flag |= mask;
	}

	private static final int clear(int flag, int bit) {
		int mask = 1 << bit;
		return flag &= ~mask;
	}

	private static final boolean get(int flag, int bit) {
		int mask = 1 << bit;
		return ((flag & mask) == mask);
	}

}
