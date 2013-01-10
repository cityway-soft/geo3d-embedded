package org.avm.device.knet.bearer;

public interface BearerManager {

	// definition des constantes pour les media de KNET
	public static final String BEARER_none = "";
	public static final String BEARER_gprs = "gprs";
	public static final String BEARER_wlan = "wlan";
	public static final String BEARER_eth = "ethernet";
	public static final String BEARER_default = "default";
	public static final String REPORT_OK = "ok";
	public static final String REPORT_KO = "ko";
	public static final String STATUS_2CONN = "toconnect";
	public static final String STATUS_CONN = "connected";
	public static final String STATUS_DECONN = "disconnected";

	public void handover(String bearerName, String ssid, String key);

	public void handover2Default();

	public String getCurrentBearerName();

	public void addBearerDevice(BearerDevice device);

	public void removeBearerDevice(BearerDevice device);
}
