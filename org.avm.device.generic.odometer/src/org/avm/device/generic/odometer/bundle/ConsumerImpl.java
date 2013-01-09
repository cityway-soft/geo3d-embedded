package org.avm.device.generic.odometer.bundle;

import org.avm.device.gps.Gps;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.position.Position;

public class ConsumerImpl extends AbstractConsumer {

	public static final String PRODUCER_PID = Gps.class.getName();

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.PID;
	}

	protected void createWires() {
		_wireadmin.createWire(PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { Position.class };
		return result;
	}

}
