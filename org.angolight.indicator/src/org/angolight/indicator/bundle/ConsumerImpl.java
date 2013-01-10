/**
 * 
 */
package org.angolight.indicator.bundle;

import org.angolight.bo.Bo;
import org.angolight.halfcycle.HalfCycle;
import org.angolight.halfcycle.HalfCycleService;
import org.angolight.kinetic.Kinetic;
import org.angolight.kinetic.KineticService;
import org.avm.device.gps.Gps;
import org.avm.elementary.can.parser.fms.PGN_F003;
import org.avm.elementary.can.parser.fms.PGN_F004;
import org.avm.elementary.can.parser.fms.PGN_F005;
import org.avm.elementary.can.parser.fms.PGN_FEE9;
import org.avm.elementary.can.parser.fms.PGN_FEF1;
import org.avm.elementary.can.parser.fms.PGN_FEF2;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;
import org.osgi.util.position.Position;

public class ConsumerImpl extends AbstractConsumer {

	public static final String GPS_PRODUCER_PID = Gps.class.getName();
	public static final String KINETIC_PRODUCER_PID = KineticService.class
			.getName();
	public static final String BO_PRODUCER_PID = Bo.class.getName();
	public static final String HALFCYCLE_PRODUCER_PID = HalfCycleService.class
			.getName();
	public static final String AVM_PRODUCER_PID = "org.avm.business.core.Avm";

	private String CAN_PRODUCER_PID;

	public ConsumerImpl(ComponentContext context, ConsumerService consumer,
			ConfigImpl config) {
		super(context, consumer, 256);
		CAN_PRODUCER_PID = config.getCanServicePid();
	}

	protected void createWires() {
		_wireadmin.createWire(GPS_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(KINETIC_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(BO_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(HALFCYCLE_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(CAN_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(AVM_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { Position.class, State.class,
				Kinetic.class, HalfCycle.class, PGN_F003.class, PGN_F004.class,
				PGN_F005.class, PGN_FEF1.class, PGN_FEF2.class, PGN_FEE9.class };
		return result;
	}

	protected String getConsumerPID() {
		return Activator.getDefault().getPid();
	}
}
