package org.avm.device.fm6000.wifi;

import java.util.Properties;


public class WifiParameters {

	private Properties _config;
	
	private String _ssid;
	
	public WifiParameters(String ssid, Properties config){
		_ssid = ssid;
		_config = config;
	}

	public String getEAPtype() {
		return (String) _config.get(WifiConfig.TAG_EAP_TYPE);
	}

	public String getSSID() {
		return _ssid;
	}

	public String getWEPKey() {
		return (String) _config.get(WifiConfig.TAG_WEPKEY);
	}

	public boolean isAdhoc() {
		boolean res = false;
		String str = (String) _config.get(WifiConfig.TAG_ADHOC);
		res = (str != null)? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public boolean isAuthentication() {
		boolean res = false;
		String str = (String) _config.get(WifiConfig.TAG_AUTHENTICATION);
		res = (str != null)? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public boolean isAutomaticWEPKey() {
		boolean res = false;
		String str = (String) _config.get(WifiConfig.TAG_AUTOMATIC_WEPKEY);
		res = (str != null)? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public boolean isEncryption() {
		boolean res = false;
		String str = (String) _config.get(WifiConfig.TAG_ENCRYPTION);
		res = (str != null)? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public boolean isOpenAuthentication() {
		boolean res = false;
		String str = (String) _config.get(WifiConfig.TAG_OPEN_AUTHENTICATION);
		res = (str != null)? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public void setAdhoc(boolean adhoc) {
		_config.put(WifiConfig.TAG_ADHOC, adhoc?"true":"false");
	}

	public void setAuthentication(boolean authentication) {
		_config.put(WifiConfig.TAG_AUTHENTICATION, authentication?"true":"false");
	}

	public void setAutomaticWEPKey(boolean automaticWEPKey) {
		_config.put(WifiConfig.TAG_AUTOMATIC_WEPKEY, automaticWEPKey?"true":"false");
	}

	public void setEAPtype(String ptype) {
		_config.put(WifiConfig.TAG_EAP_TYPE, ptype);
	}

	public void setEncryption(boolean encryption) {
		_config.put(WifiConfig.TAG_ENCRYPTION, encryption?"true":"false");
	}

	public void setOpenAuthentication(boolean openAuthentication) {
		_config.put(WifiConfig.TAG_OPEN_AUTHENTICATION, openAuthentication?"true":"false");
	}

	public void setSSID(String ssid) {
		_config.put(WifiConfig.TAG_SSID, ssid);
	}

	public void setWEPKey(String key) {
		_config.put(WifiConfig.TAG_WEPKEY, key);
	}

}
