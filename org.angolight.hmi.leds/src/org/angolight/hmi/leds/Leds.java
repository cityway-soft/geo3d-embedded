package org.angolight.hmi.leds;

import org.angolight.bo.Bo;

public interface Leds {
	public static final int ERR_DEVICE_NOT_AVAILABLE = -3;
	public static final int ERR_UNKNOWN_STATE = -4;
	
	public static final String PRIORITY_ON_BO="priority-on-bo";
	public static final String STATE_TAG="-state";
			
	
	public static final int BO_STATE_ID = 0;
	public static final int NOT_AUTHENTICATED_STATE_ID = 1;
	
	public static final String BO_STATE = Bo.class.getName();

	public int display(String state);
}
