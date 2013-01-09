package org.avm.device.knet.mmi.bundle;

import org.avm.device.knet.mmi.MmiDialogOut;
import org.avm.elementary.common.AbstractProducer;
import org.osgi.service.component.ComponentContext;

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
		Class[] result = new Class[] { MmiDialogOut.class };
		return result;
	}
}
