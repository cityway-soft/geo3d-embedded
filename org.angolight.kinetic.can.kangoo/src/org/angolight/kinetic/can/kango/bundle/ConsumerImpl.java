/**
 * 
 */
package org.angolight.kinetic.can.kango.bundle;

import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.service.component.ComponentContext;

public class ConsumerImpl extends AbstractConsumer {

	private  String PRODUCER_PID;

	public ConsumerImpl(ComponentContext context, ConsumerService consumer, ConfigImpl config) {
		super(context, consumer,1024);
		PRODUCER_PID = config.getCanServicePid();
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		createWire(PRODUCER_PID);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { PGN_102.class };
		return result;
	}

}
