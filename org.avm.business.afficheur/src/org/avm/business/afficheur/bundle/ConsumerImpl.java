package org.avm.business.afficheur.bundle;

import org.avm.business.core.Avm;
import org.avm.business.messages.Messages;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class ConsumerImpl extends AbstractConsumer {

	public static final String AVM_PRODUCER_PID = Avm.class.getName();
	public static final String MESSAGES_PRODUCER_PID = Messages.class.getName();

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		_wireadmin.createWire(AVM_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(MESSAGES_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class };
		return result;
	}

}
