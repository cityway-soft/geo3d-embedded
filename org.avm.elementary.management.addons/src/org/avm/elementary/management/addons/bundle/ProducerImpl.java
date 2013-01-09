package org.avm.elementary.management.addons.bundle;

import org.avm.elementary.common.AbstractProducer;
import org.osgi.service.component.ComponentContext;

public class ProducerImpl extends AbstractProducer {

	public ProducerImpl(ComponentContext context) {
		super(context);
	}

	protected String getProducerPID() {
		return Activator.PID;
	}

	protected Class[] getProducerFlavors() {
		Class[] result = new Class[] {};
		return result;
	}
}
