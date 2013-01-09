package org.angolight.hmi.leds.bundle;

import org.angolight.bo.Bo;
import org.angolight.device.leds.Leds;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Wire;
import org.osgi.util.measurement.State;


public class ConsumerImpl extends AbstractConsumer {

	public static final String LEDS_PRODUCER_PID = Leds.class.getName();
	public static final String BO_PRODUCER_PID = Bo.class.getName();
	public static final String INDICATOR_PRODUCER_PID = "org.angolight.indicator.Indicator";
	public static final String USERSESSION_PRODUCER_PID = UserSessionService.class.getName();

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		_wireadmin.createWire(LEDS_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(BO_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(INDICATOR_PRODUCER_PID, getConsumerPID(), null);
		
		Wire wire = _wireadmin.createWire(USERSESSION_PRODUCER_PID, getConsumerPID(), null);
		Object obj = wire.poll();
		if (obj != null){
			updated(wire, obj);
		}
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class };
		return result;
	}
}
