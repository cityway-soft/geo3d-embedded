package org.avm.device.linux.wifi.bundle;

import org.avm.device.linux.wifi.WifiConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements WifiConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getDevice() {
		return (String) _config.get(DEVICE_TAG);
	}

	public String getEssid() {
		return (String) _config.get(ESSID_TAG);
	}

	public String getKey() {
		return (String) _config.get(KEY_TAG);
	}

	public String getMode() {
		return (String) _config.get(MODE_TAG);
	}

	public String getRate() {
		return (String) _config.get(RATE_TAG);
	}

	public String getChannel() {
		return (String) _config.get(CHANNEL_TAG);
	}

	public String getFreq() {
		return (String) _config.get(FREQ_TAG);
	}

	public void setDevice(String device) {
		_config.put(DEVICE_TAG, device);
	}

	public void setEssid(String essid) {
		_config.put(ESSID_TAG, essid);
	}

	public void setKey(String key) {
		_config.put(KEY_TAG, key);
	}

	public void setMode(String mode) {
		_config.put(MODE_TAG, mode);
	}

	public void setRate(String rate) {
		_config.put(RATE_TAG, rate);
	}

	public void setFreq(String freq) {
		_config.put(FREQ_TAG, freq);
	}

	public void setChannel(String channel) {
		_config.put(CHANNEL_TAG, channel);
	}

	public String getIfDownCommand() {
		return (String) _config.get(IFDOWN_COMMAND_TAG);
	}

	public String getIfUpCommand() {
		return (String) _config.get(IFUP_COMMAND_TAG);
	}

	public void setIfUpCommand(String cmd) {
		_config.put(IFUP_COMMAND_TAG, cmd);
	}

	public void setIfDownCommand(String cmd) {
		_config.put(IFDOWN_COMMAND_TAG, cmd);
	}

}
