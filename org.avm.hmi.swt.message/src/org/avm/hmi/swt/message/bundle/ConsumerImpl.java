package org.avm.hmi.swt.message.bundle;

import org.avm.business.messages.Messages;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Wire;
import org.osgi.util.measurement.State;

public class ConsumerImpl extends AbstractConsumer {
	
	public static final String AVM_PRODUCER_PID = "org.avm.business.core.Avm";

	public static final String USERSESSION_PRODUCER_PID = UserSessionService.class.getName();

	public static final String MESSAGES_PRODUCER_PID = Messages.class.getName();
	
	public static final String ALARM_PRODUCER_PID = AlarmService.class.getName();


	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		Wire wire = _wireadmin.createWire(USERSESSION_PRODUCER_PID, getConsumerPID(), null);
		Object obj = wire.poll();
		if (obj != null){
			updated(wire, obj);
		}
		_wireadmin.createWire(MESSAGES_PRODUCER_PID, getConsumerPID(), null);

		_wireadmin.createWire(ALARM_PRODUCER_PID, getConsumerPID(), null);
		

		_wireadmin.createWire(AVM_PRODUCER_PID, getConsumerPID(), null);

	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class, Alarm.class };
		return result;
	}

}
