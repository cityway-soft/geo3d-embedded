package org.avm.business.vocal.bundle;

import org.avm.business.core.Avm;
import org.avm.business.protocol.phoebus.MessageText;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.variable.Variable;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class ConsumerImpl extends AbstractConsumer {

	public static final String AVM_PRODUCER_PID = Avm.class.getName();
	public static final String ALARM_PRODUCER_PID = AlarmService.class
			.getName();
	public static final String VARIABLE_PORTE_AVANT_PRODUCER_PID = "porteav";

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		_wireadmin.createWire(AVM_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(ALARM_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(VARIABLE_PORTE_AVANT_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class, Alarm.class,
				MessageText.class, Variable.class };
		return result;
	}
}
