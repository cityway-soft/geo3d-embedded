package org.angolight.bo;

public interface BoState {
	public static final int VminState 			= 0;
	public static final int Zone1State 			= 1;
	public static final int Zone2State 			= 2;
	public static final int Zone3State 			= 3;
	public static final int Zone4State 			= 4;
	public static final int Zone5State 			= 5;
	public static final int VmaxState 			= 6;	
	
	public static final String[] NAMES = new String[] { "vmin",
		"zone1", "zone2", "zone3", "zone4", "zone5", "vmax" };
}
