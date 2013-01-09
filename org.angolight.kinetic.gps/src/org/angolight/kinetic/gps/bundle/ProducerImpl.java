package org.angolight.kinetic.gps.bundle;

import org.angolight.kinetic.Kinetic;
import org.avm.elementary.common.AbstractProducer;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Wire;

public class ProducerImpl extends AbstractProducer {

	public ProducerImpl(ComponentContext context) {
		super(context);
	}

	protected String getProducerPID() {
		return Activator.getDefault().getPid();
	}

	protected Class[] getProducerFlavors() {
		Class[] result = new Class[] { Kinetic.class };
		return result;
	}

	public void publish(Object o) {

		if (_wireadmin == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Notification du message " + o.getClass().getName());
		}

		if (_wires != null) {
			synchronized (_mutex) {
				Kinetic kinetic = (Kinetic) o;
				kinetic.setCounter(_wires.length);
				for (int i = 0; i < _wires.length; i++) {
					Wire wire = _wires[i];
					wire.update(o);
				}
			}

		}

	}
}
