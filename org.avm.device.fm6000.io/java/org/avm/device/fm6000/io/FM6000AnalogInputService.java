package org.avm.device.fm6000.io;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.fm6000.io.jni.COMVS_IO;
import org.avm.device.fm6000.io.jni.COMVS_IO_PIN;
import org.avm.device.io.AnalogIODevice;
import org.avm.device.io.AnalogIODriver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class FM6000AnalogInputService extends ServiceTracker implements
		AnalogIODriver {

	private static final int CAPABILITY = 8;

	private static final int RESOLUTION = 96;

	private Logger _log = Logger.getInstance(this.getClass());

	private AnalogIODevice _device;

	private ServiceRegistration _sr;

	private COMVS_IO_PIN[] _data;

	public FM6000AnalogInputService(BundleContext context,
			ServiceReference device) {
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

		int handle = Activator.getDefault().getHandle();
		if (handle == 0) {
			return 0;
		}

		COMVS_IO.read(handle, _data, _data.length);
		long value = _data[index].getState();
		
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
	
	protected void initialize() {
		int n = getCapability();
		_data = new COMVS_IO_PIN[n];
		for (int i = 0; i < getCapability(); i++) {
			_data[i] = new COMVS_IO_PIN();
			_data[i].setId(COMVS_IO.IO_AIO_INPUT_0 + i);
			_data[i].setMode(COMVS_IO.IO_MODE_AIO_INPUT);
		}
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
