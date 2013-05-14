package org.angolight.hmi.swt.leds;

import java.util.Properties;

public interface LedsConfig {

	public final static String URL_CONNECTION_TAG = "url.connection";
	public final static String BRIGHTNESS_TAG = "brightness";
	public final static String SEQUENCES_TAG = "sequences";
	public final static String INSIDE_TAG = "inside";
	public static final Object OVAL_TAG = "oval";


	public String getUrlConnection();

	public void setUrlConnection(String uri);

	public Integer getBrightness();

	public void setBrightness(Integer value);

	public Properties getSequences();

	public void setSequences(Properties p);

	public boolean isInside();

	public boolean isOval();

}
