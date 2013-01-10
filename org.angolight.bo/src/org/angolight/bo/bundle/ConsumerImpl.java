/**
 * 
 */
package org.angolight.bo.bundle;

import org.angolight.kinetic.Kinetic;
import org.angolight.kinetic.KineticService;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.service.component.ComponentContext;

public class ConsumerImpl extends AbstractConsumer {

	public static final String KINETIC_PRODUCER_PID = KineticService.class
			.getName();

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected void createWires() {
		_wireadmin.createWire(KINETIC_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { Kinetic.class };
		return result;
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}
}
