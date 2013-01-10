package org.avm.hmi.mmi.avm.bundle;

import org.avm.business.core.event.Event;
import org.avm.business.protocol.phoebus.AvanceRetard;
import org.avm.business.protocol.phoebus.Message;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.messenger.Messenger;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class ConsumerImpl extends AbstractConsumer {

	public static final String AVM_PRODUCER_PID = org.avm.business.core.Avm.class
			.getName();
	public static final String MESSENGER_PRODUCER_PID = Messenger.class
			.getName();

	public ConsumerImpl(ComponentContext context, ConsumerService peer) {
		super(context, peer);
		// _log.setPriority(Priority.DEBUG);
	}

	protected String getConsumerPID() {
		return Activator.PID;
	}

	protected void createWires() {
		_wireadmin.createWire(AVM_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(MESSENGER_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class, AvanceRetard.class,
				Event.class, Message.class };
		return result;
	}

}
