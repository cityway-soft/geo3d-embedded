package org.avm.device.generic.gsm.bundle;

import org.avm.device.gsm.GsmEvent;
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
		Class[] result = new Class[] { GsmEvent.class };
		return result;
	}
}
