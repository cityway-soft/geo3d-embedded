package org.avm.business.tracking.bundle;

import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.messenger.Messenger;
import org.osgi.service.component.ComponentContext;

public class ConsumerImpl extends AbstractConsumer {

	public static final String PRODUCER_PID = Messenger.class.getName();

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		_wireadmin.createWire(PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { Object.class };
		return result;
	}

}
