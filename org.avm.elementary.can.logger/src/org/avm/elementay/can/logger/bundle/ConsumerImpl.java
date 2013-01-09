package org.avm.elementay.can.logger.bundle;

import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementay.can.logger.LoggerConfig;
import org.osgi.service.component.ComponentContext;

public class ConsumerImpl extends AbstractConsumer {

	public String PRODUCER_PID ="org.avm.device.can";

	public ConsumerImpl(ComponentContext context, ConsumerService consumer, LoggerConfig config) {
		super(context, consumer, 1024);
		PRODUCER_PID = config.getCanServicePid();
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		_wireadmin.createWire(PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { PGN.class };
		return result;
	}
}
