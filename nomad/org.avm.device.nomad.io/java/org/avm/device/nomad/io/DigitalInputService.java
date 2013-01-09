package org.avm.device.nomad.io;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;
import org.avm.device.nomad.io.jni.NOMAD_IO;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class DigitalInputService extends ServiceTracker implements
		DigitalIODriver, Runnable {
	
	public static int CAPABILITY = 6;

	private Logger _log = Logger.getInstance(this.getClass());

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	private Thread _thread;

	private int _data;

	public DigitalInputService(BundleContext context, ServiceReference device) {
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
		int mask = (1 << index);
		boolean value = ((_data & mask) == mask);

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
		int handle = NOMAD_IO.dio_open("/dev/i2c-0");
		
		while (_thread != null && !_thread.isInterrupted()) {
			int result = NOMAD_IO.dio_read_input(handle);
			int mask = 1;
			for (int i = 0; i < CAPABILITY; i++) {
				if ((_data & mask) != (result & mask)) {
					_listeners
							.fireIndexedPropertyChange(null, i,
									((_data & mask) == mask),
									((result & mask) == mask));
				}
				mask <<= 1;
			}
			_data = result;
		}

		NOMAD_IO.dio_close(handle);
	}
}
