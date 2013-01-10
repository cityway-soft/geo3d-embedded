package org.avm.device.nomad.io;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.io.AnalogIODevice;
import org.avm.device.io.AnalogIODriver;
import org.avm.device.nomad.io.jni.NOMAD_IO;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class AnalogInputService extends ServiceTracker implements
		AnalogIODriver {

	private static final int CAPABILITY = 2;

	private static final int RESOLUTION = 12;

	private Logger _log = Logger.getInstance(this.getClass());

	private AnalogIODevice _device;

	private ServiceRegistration _sr;

	private int _handle;

	public AnalogInputService(BundleContext context, ServiceReference device) {
		super(context, device, null);
	}

	public int getCapability() {
		return CAPABILITY;
	}

	public int getBitResolution() {
		return RESOLUTION;
	}

	public long getValue(int index) {
		if (index >= CAPABILITY)
			throw new IndexOutOfBoundsException();

		int value = NOMAD_IO.adc_read_input(_handle, index);

		if (_log.isDebugEnabled()) {
			_log.debug("I/O[" + index + "] = " + value);
		}
		return value;
	}

	public void setValue(int index, long value) {
		throw new UnsupportedOperationException();
	}

	public Object addingService(ServiceReference reference) {
		_device = (AnalogIODevice) super.addingService(reference);
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
		_handle = NOMAD_IO.adc_open("/dev/i2c-0");
		if (_handle < 0) {
			return;
		}
	}

	private void dispose() {
		if (_handle > 0) {
			NOMAD_IO.adc_close(_handle);
			_handle = 0;
		}
	}

}
