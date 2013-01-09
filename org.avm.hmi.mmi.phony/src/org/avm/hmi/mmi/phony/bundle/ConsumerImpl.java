package org.avm.hmi.mmi.phony.bundle;

import org.avm.business.protocol.phoebus.Message;
import org.avm.device.phony.PhoneEvent;
import org.avm.device.phony.Phony;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.messenger.Messenger;
import org.avm.hmi.mmi.phony.MmiPhony;
import org.osgi.service.component.ComponentContext;

public class ConsumerImpl extends AbstractConsumer {

	public static final String PHONY_PRODUCER_PID = Phony.class.getName();
	public static final String MESSENGER_PRODUCER_PID = Messenger.class
			.getName();

	public ConsumerImpl(ComponentContext context, MmiPhony peer) {
		super(context, peer);
	}

	protected String getConsumerPID() {
		return Activator.PID;
	}

	protected void createWires() {
		_wireadmin.createWire(PHONY_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(MESSENGER_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { PhoneEvent.class, Message.class };
		return result;
	}

}
