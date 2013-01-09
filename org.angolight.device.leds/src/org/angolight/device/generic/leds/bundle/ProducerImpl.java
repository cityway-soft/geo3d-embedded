package org.angolight.device.generic.leds.bundle;

import org.avm.elementary.common.AbstractProducer;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class ProducerImpl extends AbstractProducer {

	public ProducerImpl(ComponentContext context) {
		super(context);
	}

	protected String getProducerPID() {
		return Activator.getDefault().getPid();
	}

	protected Class[] getProducerFlavors() {
		Class[] result = new Class[] { State.class };
		return result;
	}
}
