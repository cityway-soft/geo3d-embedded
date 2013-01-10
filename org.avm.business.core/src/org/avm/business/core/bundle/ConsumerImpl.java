package org.avm.business.core.bundle;

import org.avm.business.protocol.phoebus.Planification;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.geofencing.Balise;
import org.avm.elementary.geofencing.GeoFencing;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.wireadmin.Wire;
import org.osgi.util.measurement.State;



public class ConsumerImpl extends AbstractConsumer {

	public static final String GEOFENCING_PRODUCER_PID = GeoFencing.class.getName();

	public static final String MESSENGER_PRODUCER_PID = Messenger.class.getName();

	public static final String USERSESSION_PRODUCER_PID = UserSessionService.class.getName();


	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}

	protected void createWires() {
		_wireadmin.createWire(GEOFENCING_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(MESSENGER_PRODUCER_PID, getConsumerPID(), null);
		Wire wire = _wireadmin.createWire(USERSESSION_PRODUCER_PID, getConsumerPID(), null);
		Object obj = wire.poll();
		if (obj != null){
			updated(wire, obj);
		}
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { Balise.class, Planification.class, State.class};
		return result;
	}

}
