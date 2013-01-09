package org.avm.elementary.management.addons.bundle;

import org.avm.business.protocol.management.Management;
import org.avm.elementary.common.AbstractConsumer;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.messenger.Messenger;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class ConsumerImpl extends AbstractConsumer {

	public static final String MESSENGER_PRODUCER_PID = Messenger.class
			.getName();

	public static final String WIFI_PRODUCER_PID = "org.avm.device.wifi.Wifi";
	public static final String USB_PRODUCER_PID = "org.avm.device.usbmass.Usbmass";

	public ConsumerImpl(ComponentContext context, ConsumerService consumer) {
		super(context, consumer);
	}

	protected String getConsumerPID() {
		return Activator.PID;
	}

	protected void createWires() {
		_wireadmin.createWire(MESSENGER_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(WIFI_PRODUCER_PID, getConsumerPID(), null);
		_wireadmin.createWire(USB_PRODUCER_PID, getConsumerPID(), null);
	}

	protected Class[] getConsumerFlavors() {
		Class[] result = new Class[] { State.class, Management.class };
		return result;
	}

}
