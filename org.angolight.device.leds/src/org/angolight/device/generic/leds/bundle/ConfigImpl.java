package org.angolight.device.generic.leds.bundle;

import org.angolight.device.generic.leds.LedsConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements LedsConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getUrlConnection() {
		return (String) _config.get(URL_CONNECTION_TAG);
	}

	public void setUrlConnection(String uri) {
		_config.put(URL_CONNECTION_TAG, uri);
	}

	public Integer getBrightness() {
		return (Integer) _config.get(BRIGHTNESS_TAG);
	}

	public void setBrightness(Integer value) {
		_config.put(BRIGHTNESS_TAG, value);
	}



}
