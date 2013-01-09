package org.avm.elementary.geofencing.bundle;

import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.geofencing.Balise;
import org.osgi.service.component.ComponentContext;

public class ProducerImpl extends AbstractProducer {
	public ProducerImpl(ComponentContext context) {
		super(context);
	}

	protected String getProducerPID() {
		return Activator.getDefault().getPid();
	}

	protected Class[] getProducerFlavors() {
		Class[] result = new Class[] { Balise.class };
		return result;
	}
}
