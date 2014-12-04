package org.avm.device.ihmi.z8;

public class Z8Helper {
	private static String[] states = { "Undefined", "Powered Down",
			"Powered down with wakeup timer",
			"Powered up with ignition signal", "Standby",
			"Powered up after wakeup", "Waiting for shutdown",
			"Waiting for shutdown too hot",
			"Waiting for system to cool down power down",
			"Waiting for system to cool standby" };

	public static String systemStateToText(int state) {
		if (state > states.length || state < 0) {
			state = 0;
		}
		return states[state];
	}
}
