package org.avm.device.linux.wifi;

public interface WifiConfig {

	public static String DEVICE_TAG = "device";

	public static String ESSID_TAG = "essid";

	public static String KEY_TAG = "key";

	public static String MODE_TAG = "mode";
	
	public static String CHANNEL_TAG = "channel";

	public static String RATE_TAG = "rate";
	
	public static String FREQ_TAG = "freq";

	public String getDevice();

	public void setDevice(String device);

	public String getEssid();

	public void setEssid(String essid);

	public String getKey();

	public void setKey(String key);
	
	public String getMode();

	public void setMode(String mode);
	
	public String getChannel();

	public void setChannel(String channel);
	
	public String getRate();

	public void setRate(String rate);
	
	public String getFreq();

	public void setFreq(String freq);


}