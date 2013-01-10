package org.avm.hmi.swt.phony.bundle;

import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmEvent;
import org.avm.device.phony.PhoneEvent;
import org.avm.device.phony.Phony;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Wire;
import org.osgi.util.measurement.State;

public class ConsumerImpl extends AbstractConsumer {

	public static final String PHONY_PRODUCER_PID = Phony.class.getName();
	public static final String GSM_PRODUCER_PID = Gsm.class.getName();
	public static final String USERSESSION_PRODUCER_PID = UserSessionService.class.getName();


	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		_wireadmin.createWire(PHONY_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(GSM_PRODUCER_PID, getConsumerPID(), null);
		Wire wire = _wireadmin.createWire(USERSESSION_PRODUCER_PID, getConsumerPID(), null);
		Object obj = wire.poll();
		if (obj != null){
			updated(wire, obj);
		}
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class, GsmEvent.class,
				PhoneEvent.class };
		return result;
	}

}
