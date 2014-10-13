package org.angolight.device.generic.leds;
 

public interface LedsConfig {

	public final static String URL_CONNECTION_TAG = "url.connection";
	public final static String BRIGHTNESS_TAG = "brightness";

	public String getUrlConnection();

	public void setUrlConnection(String uri);

	public Integer getBrightness();

	public void setBrightness(Integer value);


}
