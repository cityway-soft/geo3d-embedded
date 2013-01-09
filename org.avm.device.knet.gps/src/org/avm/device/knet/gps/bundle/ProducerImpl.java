package org.avm.device.knet.gps.bundle;

import org.avm.elementary.common.AbstractProducer;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.position.Position;

public class ProducerImpl extends AbstractProducer {

	public ProducerImpl(ComponentContext context) {
		super(context);
	}

	// public ProducerImpl(ComponentContext context, WireAdmin wa) {
	// super(context, wa);
	// }

	protected String getProducerPID() {
		return Activator.PID;
	}

	/**
	 * @see org.avm.elementary.library.AbstractProducer#getProducerFlavors()
	 */
	protected Class[] getProducerFlavors() {
		Class[] result = new Class[] { Position.class };
		return result;
	}
}
