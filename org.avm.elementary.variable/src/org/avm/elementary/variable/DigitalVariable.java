package org.avm.elementary.variable;

import org.avm.device.io.DigitalIODevice;
import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.common.IndexedPropertyChangeEvent;
import org.avm.elementary.common.PropertyChangeEvent;
import org.avm.elementary.common.PropertyChangeListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.Measurement;

public class DigitalVariable extends AbstractVariable implements
		PropertyChangeListener {

	protected Producer _producer;

	protected DigitalIODevice _device;

	public DigitalVariable() {
		super();
	}

	public Measurement getValue() {
		Measurement result = null;
		if (_device != null) {
			_log.debug("IO = " + getDeviceIndex() + " "
					+ _device.getValue(getDeviceIndex()));
			double value = (_device.getValue(getDeviceIndex())) ? 1d : 0d;
			result = new Measurement(value);
		}
		return result;
	}

	public void setValue(Measurement value) {
		if (_device != null) {
			_device.setValue(getDeviceIndex(), (value.getValue() > 0));
		}
	}

	public Object addingService(ServiceReference reference) {
		_device = (DigitalIODevice) super.addingService(reference);
		_device.addPropertyChangeListener(getDeviceIndex(), this);
		_producer = new Producer(_context);
		_producer.start();
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		_producer.stop();
		_producer = null;
		_device.removePropertyChangeListener(getDeviceIndex(), this);
		_device = null;
		super.removedService(reference, service);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		IndexedPropertyChangeEvent event = (IndexedPropertyChangeEvent) evt;
		_log.debug("notify I/O[" + event.getIndex() + "] = "
				+ event.getNewValue());
		if (_producer != null)
			_producer.publish(this);
	}

	class Producer extends AbstractProducer {

		public Producer(ComponentContext context) {
			super(context);
		}

		protected Class[] getProducerFlavors() {
			Class[] result = new Class[] { DigitalVariable.class };
			return result;
		}

		protected String getProducerPID() {
			return getName();
		}

	}

}
