package org.angolight.hmi.leds;

import java.util.Properties;

public interface LedsConfig {
	public final static String SEQUENCES_TAG = "sequences";
	
	public static final String STATE_LIST = "state-list";


	
	public Properties getSequences();

	public void setSequences(Properties p);


}
