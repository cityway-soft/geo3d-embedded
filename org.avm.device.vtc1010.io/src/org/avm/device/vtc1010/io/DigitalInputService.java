package org.avm.device.vtc1010.io;

import java.util.BitSet;
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

public class DigitalInputService extends ServiceTracker implements
		DigitalIODriver, Runnable {

	public static int CAPABILITY = 3;

	public static int OFFSET = 3;

	private Logger _log = Logger.getInstance(this.getClass().getName());

	private BitSet _data = new BitSet();

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	private Thread _thread;

	private Vtc1010IO ioAccess = null;

	public Vtc1010IO getIoAccess() {
		return ioAccess;
	}

	public void setIoAccess(Vtc1010IO ioAccess) {
		this.ioAccess = ioAccess;
	}

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
		boolean value = _data.get(index+OFFSET);
		if (_log.isDebugEnabled()) {
			_log.debug("I/O[" + index + "] = " + value);
			//_log.debug("data : " + _data.);
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
		if (ioAccess != null) {
			int handle = ioAccess.open();
			while (_thread != null && !_thread.isInterrupted()) {
				int value = ioAccess.read(handle, Vtc1010IO.BLOCKING_READ);
				System.out.println("Read 0x" + Integer.toHexString(value));
				if (_log.isDebugEnabled()) {
					_log.debug("Read 0x" + Integer.toHexString(value));
				}
				BitSet old = (BitSet) _data.clone();
				for (int i = OFFSET; i < CAPABILITY + OFFSET; i++) {
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
			ioAccess.close(handle);
		}else{
			System.out.println("no ioAccess");
		}
	}

	private static final boolean get(int flag, int bit) {
		int mask = 1 << bit;
		return ((flag & mask) == mask);
	}

}
