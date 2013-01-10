package org.avm.device.nomad.io;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.io.CounterDevice;
import org.avm.device.io.CounterDriver;
import org.avm.device.nomad.io.jni.NOMAD_IO;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class CounterService extends ServiceTracker implements CounterDriver {

	private static final int CAPABILITY = 2;

	private static final int RESOLUTION = 16;

	private Logger _log = Logger.getInstance(this.getClass());

	private CounterDevice _device;

	private ServiceRegistration _sr;

	private int _handle;

	private int[] _data = new int[CAPABILITY];

	public CounterService(BundleContext context, ServiceReference device) {
		super(context, device, null);
		_log.setPriority(Priority.DEBUG);
	}

	public int getCapability() {
		return CAPABILITY;
	}

	public int getBitResolution() {
		return RESOLUTION;
	}

	public int getValue(int index) {
		if (index >= CAPABILITY)
			throw new IndexOutOfBoundsException();

		int value = NOMAD_IO.dio_read_counter(_handle, index);
		if (value != _data[index]) {
			_data[index] = value;
			if (_log.isDebugEnabled()) {
				_log.debug("I/O[" + index + "] = " + value);
			}
		}
		
		return _data[index];
	}

	public void reset(int index) {
		// TODO Auto-generated method stub

	}

	public Object addingService(ServiceReference reference) {
		_device = (CounterDevice) super.addingService(reference);
		_device.setDriver(this);
		initialize();
		_sr = context.registerService(this.getClass().getName(), this,
				new Properties());
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		_sr.unregister();
		dispose();
		_device.setDriver(null);
		_device = null;
	}

	private void initialize() {
		_handle = NOMAD_IO.dio_open("/dev/i2c-0");
		if (_handle < 0) {
			return;
		}
	}

	private void dispose() {
		if (_handle > 0) {
			NOMAD_IO.dio_close(_handle);
			_handle = 0;
		}
	}

}
