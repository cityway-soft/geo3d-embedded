package org.avm.elementary.media.test.bundle;

import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.service.component.ComponentContext;

public class ConsumerImpl extends AbstractConsumer {


	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.PID;
	}

	protected void createWires() {
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { };
		return result;
	}

}
