/**
 * 
 */
package org.angolight.kinetic.can.fms.bundle;

import org.avm.elementary.can.parser.fms.PGN_FEF1;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.service.component.ComponentContext;

public class ConsumerImpl extends AbstractConsumer {

	private String PRODUCER_PID;

	public ConsumerImpl(ComponentContext context, ConsumerService consumer,
			ConfigImpl config) {
		super(context, consumer, 1024);
		PRODUCER_PID = config.getCanServicePid();
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		createWire(PRODUCER_PID);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { PGN_FEF1.class };
		return result;
	}

}
