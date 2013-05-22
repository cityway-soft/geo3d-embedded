package org.avm.device.linux.io;

import java.io.FileInputStream;
import java.util.BitSet;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class DigitalInputService extends ServiceTracker implements
		DigitalIODriver, Runnable {

	private static final String DEVICE = "/dev/vsi0";

	public static int CAPABILITY = 16;

	private Logger _log = Logger.getInstance(this.getClass().getName());

	private BitSet _data = new BitSet();

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	private Thread _thread;

	public DigitalInputService(BundleContext context, ServiceReference device) {
		super(context, device, null);
		_listeners = new PropertyChangeSupport(this);
		// _log.setPriority(Priority.DEBUG);
	}

	public int getCapability() {
		return CAPABILITY;
	}

	public boolean getValue(int index) {
		if (index >= CAPABILITY)
			throw new IndexOutOfBoundsException();
		boolean value = _data.get(index);
		if (_log.isDebugEnabled()) {
			_log.debug("I/O[" + index + "] = " + value);
		}
		return value;
	}

	public void setValue(int index, boolean value) {
		throw new UnsupportedOperationException();
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
		start();
		_sr = context.registerService(this.getClass().getName(), this,
				new Properties());
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		_device.setDriver(null);
		context.ungetService(reference);
		stop();
		_sr.unregister();
		_device = null;
	}

	protected void start() {
		if (_thread == null) {
			_thread = new Thread(this);
			_thread.setName("[AVM] digital io");
			_thread.setDaemon(true);
			_thread.start();
		}
	}

	protected void stop() {
		if (_thread != null && !_thread.isInterrupted()) {
			_thread.interrupt();
			_thread = null;
		}
	}

	public void run() {
		FileInputStream in;
		try {

			in = new FileInputStream(DEVICE);
			byte[] buffer = new byte[2];

			while (_thread != null && !_thread.isInterrupted()) {
				if (in.read(buffer) != 2) {
					break;
				}
				int value = ((buffer[1] << 8) | buffer[0]) & 0xffff;
				if (_log.isDebugEnabled()) {
					_log.debug("Read 0x" + Integer.toHexString(value));
				}

				BitSet old = (BitSet) _data.clone();
				for (int i = 0; i < 16; i++) {
					if (get(value, i)) {
						_data.clear(i);
					} else {
						_data.set(i);
					}
					if (_data.get(i) != old.get(i)) {
						_listeners.fireIndexedPropertyChange(null, i,
								old.get(i), _data.get(i));
					}
				}
			}
			in.close();
		} catch (Exception e) {
			_log.error(e);
		}
	}

	private static final boolean get(int flag, int bit) {
		int mask = 1 << bit;
		return ((flag & mask) == mask);
	}

}
