package org.avm.business.recorder.bundle;

import org.apache.log4j.Priority;
import org.avm.business.core.Avm;
import org.avm.business.core.event.Arret;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.variable.DigitalVariable;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;
import org.osgi.util.position.Position;

public class ConsumerImpl extends AbstractConsumer {

	public static final String AVM_PRODUCER_PID = Avm.class.getName();
	public static final String PORTE_AV_PRODUCER_PID = "porte_av";
	public static final String PORTE_AR_PRODUCER_PID = "porte_ar";
	public static final String GPS_PRODUCER_PID = "org.avm.device.gps.Gps";

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
		_log.setPriority(Priority.DEBUG);
	}

	protected String getConsumerPID() {
		return Activator.PID;
	}

	protected void createWires() {
		_wireadmin.createWire(AVM_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(PORTE_AV_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(PORTE_AR_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(GPS_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class, Arret.class,
				DigitalVariable.class, Position.class };
		return result;
	}

}
