package org.avm.elementary.variable;

import org.avm.device.io.CounterDevice;
import org.osgi.framework.ServiceReference;
import org.osgi.util.measurement.Measurement;

public class CounterVariable extends AbstractVariable implements Runnable {

	private int _resolution, _old, _current;

	private int _value;

	private Thread _target;

	protected CounterDevice _device;

	private boolean _running;

	public Measurement getValue() {
		Measurement result = new Measurement(_value, getUnit());
		return result;
	}

	public void setValue(Measurement value) {
		_value = (int) value.getValue();
	}

	public Object addingService(ServiceReference reference) {
		_device = (CounterDevice) super.addingService(reference);
		_current = _device.getValue(getDeviceIndex());
		_old = _current;
		_resolution = (1 << _device.getBitResolution()) - 1;
		_running = true;
		_target = new Thread(this);
		_target.setName("[AVM] variable");
		_target.start();
		_log.info("Device added " + _device);
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		_log.info("Device removed " + _device);
		_running = false;
		_device = null;
		super.removedService(reference, service);
	}

	public void run() {
		int offset = 0;
		while (_running) {
			if (_device != null) {
				_current = _device.getValue(getDeviceIndex());
				if (_current != _old) {
					if (_current >= _old)
						offset = _current - _old;
					else
						offset = _resolution - _old + _current;
					_old = _current;
					_value += offset;
					// System.out.println("[DSU] current = " + _current);
					// System.out.println("[DSU] offset = " + offset);
				}
			}
			sleep(100);
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException("Error : " + e.getMessage());
		}
	}

}
