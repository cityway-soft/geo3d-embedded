package org.avm.business.tracking.bundle;

import org.avm.business.protocol.phoebus.DemandeStatut;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.messenger.Messenger;
import org.osgi.service.component.ComponentContext;

public class ConsumerImpl extends AbstractConsumer {

	public static final String MESSENGER_PRODUCER_PID = Messenger.class
			.getName();

	public ConsumerImpl(final ComponentContext context,
			final ConsumerService consumer) {

		super(context, consumer);
	}

	protected void createWires() {

		this._wireadmin.createWire(ConsumerImpl.MESSENGER_PRODUCER_PID,
				this.getConsumerPID(), null);

	}

	protected Class[] getConsumerFlavors() {

		final Class[] result = new Class[] { DemandeStatut.class };
		return result;
	}

	protected String getConsumerPID() {

		return Activator.getDefault().getPid();
	}
}
