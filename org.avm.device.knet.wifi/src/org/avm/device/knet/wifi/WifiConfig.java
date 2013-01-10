package org.avm.device.knet.wifi;

public interface WifiConfig {

	public static String TAG_SSID = "org.avm.device.wifi.ssid";

	public static String DEFAULT_SSID = "wirmacat22";

	public static String TAG_ENCRYPTION = "org.avm.device.wifi.encryption";

	public static String DEFAULT_ENCRYPTION = "false";

	public static String TAG_OPEN_AUTHENTICATION = "org.avm.device.wifi.open-authentication";

	public static String DEFAULT_OPEN_AUTHENTICATION = "true";

	public static String TAG_ADHOC = "org.avm.device.wifi.adhoc";

	public static String DEFAULT_ADHOC = "false";

	public static String TAG_WEPKEY = "org.avm.device.wifi.wepkey";

	public static String DEFAULT_WEPKEY = "ED8B0F1D22";

	public static String TAG_AUTOMATIC_WEPKEY = "org.avm.device.wifi.automatic-wepkey";

	public static String DEFAULT_AUTOMATIC_WEPKEY = "true";

	public static String TAG_AUTHENTICATION = "org.avm.device.wifi.authentication";

	public static String DEFAULT_AUTHENTICATION = "false";

	public static String TAG_EAP_TYPE = "org.avm.device.wifi.eap";

	public static String DEFAULT_EAP_TYPE = "TLS";

	public static String TAG_DISCONNECT_TIMEOUT = "org.avm.device.wifi.disconnect.timeout";

	public static String DEFAULT_DISCONNECT_TIMEOUT = "60";

	public abstract boolean isAdhoc();

	public abstract void setAdhoc(boolean adhoc);

	public abstract boolean isAuthentication();

	public abstract void setAuthentication(boolean authentication);

	public abstract boolean isAutomaticWEPKey();

	public abstract void setAutomaticWEPKey(boolean automaticWEPKey);

	public abstract String getEAPtype();

	public abstract void setEAPtype(String ptype);

	public abstract boolean isEncryption();

	public abstract void setEncryption(boolean encryption);

	public abstract boolean isOpenAuthentication();

	public abstract void setOpenAuthentication(boolean openAuthentication);

	public abstract String getSSID();

	public abstract void setSSID(String ssid);

	public abstract String getWEPKey();

	public abstract void setWEPKey(String key);

}