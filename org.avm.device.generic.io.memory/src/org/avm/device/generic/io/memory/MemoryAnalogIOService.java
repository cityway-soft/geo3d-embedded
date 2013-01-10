package org.avm.device.generic.io.memory;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.io.AnalogIODevice;
import org.avm.device.io.AnalogIODriver;
import org.avm.device.io.DigitalIODevice;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class MemoryAnalogIOService extends ServiceTracker implements
		AnalogIODriver {

	private Logger _log = Logger.getInstance(this.getClass());

	private long[] _data = new long[64];

	private AnalogIODevice _device;

	private ServiceRegistration _sr;

	public MemoryAnalogIOService(BundleContext context, ServiceReference device) {
		super(context, device, null);
	}

	public int getCapability() {
		return _data.length;
	}

	public int getBitResolution() {
		return 64;
	}

	public long getValue(int index) {
		if (index >= _data.length)
			throw new IndexOutOfBoundsException();

		_log.info("I/O[" + index + "] = " + _data[index]);
		long value = _data[index];
		return value;
	}

	public void setValue(int index, long value) {
		if (index >= _data.length)
			throw new IndexOutOfBoundsException();
		if (_device == null)
			return;
		long oldValue = _data[index];
		_data[index] = value;
		_log.info("I/O[" + index + "] = " + _data[index]);
	}

	public Object addingService(ServiceReference reference) {
		_device = (AnalogIODevice) super.addingService(reference);
		_device.setDriver(this);
		_sr = context.registerService(this.getClass().getName(), this,
				new Properties());
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		_device.setDriver(null);
		_sr.unregister();
		_device = null;
	}
}
