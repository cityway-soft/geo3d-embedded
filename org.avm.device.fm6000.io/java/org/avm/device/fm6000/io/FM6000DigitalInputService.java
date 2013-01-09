package org.avm.device.fm6000.io;

import java.util.BitSet;
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

public class FM6000DigitalInputService extends ServiceTracker implements
		DigitalIODriver, Runnable {
	public static int CAPABILITY = 6;

	private Logger _log = Logger.getInstance(this.getClass());

	private COMVS_IO_PIN[] _buffer;

	private BitSet _data = new BitSet();

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	private Thread _thread;

	public FM6000DigitalInputService(BundleContext context,
			ServiceReference device) {
		super(context, device, null);
		_listeners = new PropertyChangeSupport(this);
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
		// close();
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

	protected void initialize() {
		int n = getCapability();
		_buffer = new COMVS_IO_PIN[n];
		for (int i = 0; i < getCapability(); i++) {
			_buffer[i] = new COMVS_IO_PIN();
			_buffer[i].setId(COMVS_IO.IO_DIO_INPUT_0 + i);
			_buffer[i].setMode(COMVS_IO.IO_MODE_DIO_INPUT);
			_buffer[i].setState(0);
			_buffer[i].setChange(0);
			_buffer[i].setOption1(COMVS_IO.IO_EVENT_READ);
			_buffer[i].setOption2(COMVS_IO.IO_NOTIMEOUT);
		}

	}

	protected void dispose() {
		int n = getCapability();
		if (_buffer != null) {
			for (int i = 0; i < getCapability(); i++) {
				_buffer[i] = null;
			}
			_buffer = null;
		}
	}

	public void run() {

		int handle = Activator.getDefault().getHandle();
		initialize();

		while (_thread != null && !_thread.isInterrupted()) {

			long n = COMVS_IO.read(handle, _buffer, _buffer.length);

			BitSet old = (BitSet) _data.clone();
			for (int i = 0; i < n; i++) {
				if (_buffer[i].getState() != 0) {
					_data.set(i);
				} else {
					_data.clear(i);
				}

				if (_data.get(i) != old.get(i)) {
						_listeners.fireIndexedPropertyChange(null, i, old.get(i),
							_data.get(i));
				}
			}
		}
		dispose();
	}
}
