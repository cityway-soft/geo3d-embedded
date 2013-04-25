package org.avm.business.ecall.bundle;

import org.avm.business.protocol.phoebus.ClotureAlerte;
import org.avm.business.protocol.phoebus.PriseEnCharge;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.variable.DigitalVariable;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class ConsumerImpl extends AbstractConsumer {

	public static final String VARIABLE_ALARM_PRODUCER_PID = "alarm";
	
	public static final String ALARM_PRODUCER_PID = AlarmService.class
			.getName();
	public static final String MESSENGER_PRODUCER_PID = Messenger.class
			.getName();

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		_wireadmin.createWire(VARIABLE_ALARM_PRODUCER_PID, getConsumerPID(),
				null);
		_wireadmin.createWire(ALARM_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(MESSENGER_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { DigitalVariable.class,
				PriseEnCharge.class, ClotureAlerte.class, State.class };
		return result;
	}

}
