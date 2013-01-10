package org.avm.device.knet.wifi.bundle;

import org.avm.device.knet.wifi.WifiConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements WifiConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getEAPtype() {
		return (String) _config.get(TAG_EAP_TYPE);
	}

	public String getSSID() {
		return (String) _config.get(TAG_SSID);
	}

	public String getWEPKey() {
		return (String) _config.get(TAG_WEPKEY);
	}

	public boolean isAdhoc() {
		boolean res = false;
		String str = (String) _config.get(TAG_ADHOC);
		res = (str != null) ? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public boolean isAuthentication() {
		boolean res = false;
		String str = (String) _config.get(TAG_AUTHENTICATION);
		res = (str != null) ? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public boolean isAutomaticWEPKey() {
		boolean res = false;
		String str = (String) _config.get(TAG_AUTOMATIC_WEPKEY);
		res = (str != null) ? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public boolean isEncryption() {
		boolean res = false;
		String str = (String) _config.get(TAG_ENCRYPTION);
		res = (str != null) ? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public boolean isOpenAuthentication() {
		boolean res = false;
		String str = (String) _config.get(TAG_OPEN_AUTHENTICATION);
		res = (str != null) ? (str.equalsIgnoreCase("true")) : false;
		return res;
	}

	public void setAdhoc(boolean adhoc) {
		_config.put(TAG_ADHOC, adhoc ? "true" : "false");
	}

	public void setAuthentication(boolean authentication) {
		_config.put(TAG_AUTHENTICATION, authentication ? "true" : "false");
	}

	public void setAutomaticWEPKey(boolean automaticWEPKey) {
		_config.put(TAG_AUTOMATIC_WEPKEY, automaticWEPKey ? "true" : "false");
	}

	public void setEAPtype(String ptype) {
		_config.put(TAG_EAP_TYPE, ptype);
	}

	public void setEncryption(boolean encryption) {
		_config.put(TAG_ENCRYPTION, encryption ? "true" : "false");
	}

	public void setOpenAuthentication(boolean openAuthentication) {
		_config.put(TAG_OPEN_AUTHENTICATION, openAuthentication ? "true"
				: "false");
	}

	public void setSSID(String ssid) {
		_config.put(TAG_SSID, ssid);
	}

	public void setWEPKey(String key) {
		_config.put(TAG_WEPKEY, key);
	}

}
