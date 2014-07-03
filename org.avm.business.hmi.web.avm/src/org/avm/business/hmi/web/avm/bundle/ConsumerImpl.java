package org.avm.business.hmi.web.avm.bundle;


import org.avm.business.core.event.Event;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Wire;
import org.osgi.util.measurement.State;

public class ConsumerImpl extends AbstractConsumer {

	public static final String AVM_PRODUCER_PID = org.avm.business.core.Avm.class
			.getName();
	public static final String USERSESSION_PRODUCER_PID = UserSessionService.class
			.getName();

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return AvmComponent.class.getName();
	}

	protected void createWires() {
		_wireadmin.createWire(AVM_PRODUCER_PID, getConsumerPID(), null);
		Wire wire = _wireadmin.createWire(USERSESSION_PRODUCER_PID,
				getConsumerPID(), null);
		Object obj = wire.poll();
		if (obj != null) {
			updated(wire, obj);
		}
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class, 
				Event.class };
		return result;
	}

}
