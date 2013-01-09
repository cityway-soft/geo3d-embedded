package org.avm.device.fm6000.wifi;

import java.util.Dictionary;
import java.util.Properties;

public interface WifiConfig {

	public static String TAG_SSID = "ssid";

	public static String DEFAULT_SSID = "avm-site";

	public static String TAG_ENCRYPTION = "encryption";

	public static String DEFAULT_ENCRYPTION = "false";

	public static String TAG_OPEN_AUTHENTICATION = "open-authentication";

	public static String DEFAULT_OPEN_AUTHENTICATION = "true";

	public static String TAG_ADHOC = "adhoc";

	public static String DEFAULT_ADHOC = "false";

	public static String TAG_WEPKEY = "wepkey";

	public static String DEFAULT_WEPKEY = "";

	public static String TAG_AUTOMATIC_WEPKEY = "automatic-wepkey";

	public static String DEFAULT_AUTOMATIC_WEPKEY = "true";

	public static String TAG_AUTHENTICATION = "authentication";

	public static String DEFAULT_AUTHENTICATION = "false";

	public static String TAG_EAP_TYPE = "eap";

	public static String DEFAULT_EAP_TYPE = "TLS";

	public void addProperty(String key, Properties properties);

	public void removeProperty(String key);

	public Properties getProperty(String key);

	public Dictionary getProperties();

}