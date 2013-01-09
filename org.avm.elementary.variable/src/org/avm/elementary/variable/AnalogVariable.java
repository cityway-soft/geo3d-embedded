package org.avm.elementary.variable;

import org.avm.device.io.AnalogIODevice;
import org.osgi.framework.ServiceReference;
import org.osgi.util.measurement.Measurement;

public class AnalogVariable extends AbstractVariable {

	public static final String SCALE = "org.avm.elementary.variable.scale";

	public static final String OFFSET = "org.avm.elementary.variable.offset";

	protected AnalogIODevice _device;

	private double _a = 1;

	private double _b = 0;

	public Measurement getValue() {
		Measurement result = null;
		if (_device != null) {
			long x = _device.getValue(getDeviceIndex());
			double y = (_a * x + _b);
			result = new Measurement(y, getUnit());
		}
		return result;
	}

	public void setValue(Measurement value) {
		if (_device != null) {
			double y = value.getValue();
			double x = (y - _b) / _a;
			_device.setValue(getDeviceIndex(), (int) x);
		}
	}

	public Object addingService(ServiceReference reference) {
		_device = (AnalogIODevice) super.addingService(reference);
		_a = Double.parseDouble(getProperty(SCALE, "1.0"));
		_b = Double.parseDouble(getProperty(OFFSET, "0.0"));
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		_device = null;
		super.removedService(reference, service);
	}
}
