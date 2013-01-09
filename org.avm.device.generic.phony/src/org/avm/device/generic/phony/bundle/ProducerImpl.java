package org.avm.device.generic.phony.bundle;

import org.avm.device.phony.PhoneEvent;
import org.avm.elementary.common.AbstractProducer;
import org.osgi.service.component.ComponentContext;

public class ProducerImpl extends AbstractProducer {

	public ProducerImpl(ComponentContext context) {
		super(context);
	}

	protected String getProducerPID() {
		return Activator.getDefault().getPid();
	}

	protected Class[] getProducerFlavors() {
		Class[] result = new Class[] { PhoneEvent.class };
		return result;
	}
}
