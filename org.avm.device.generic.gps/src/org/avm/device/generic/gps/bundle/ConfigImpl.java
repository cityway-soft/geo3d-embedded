package org.avm.device.generic.gps.bundle;

import org.avm.device.generic.gps.GpsConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements GpsConfig {

	public static String URL_CONNECTION_TAG = "url.connection";
	public static String DELAY_TAG = "delay";
	public static String CORRECT_TAG = "correct";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getUrlConnection() {
		return (String) _config.get(URL_CONNECTION_TAG);
	}

	public void setUrlConnection(String uri) {
		_config.put(URL_CONNECTION_TAG, uri);
	}

	public Boolean getCorrect() {
		return (Boolean) _config.get(CORRECT_TAG);
	}

	public Double getDelay() {
		return (Double) _config.get(DELAY_TAG);
	}

	public void setCorrect(Boolean correct) {
		_config.put(CORRECT_TAG, correct);
	}

	public void setDelay(Double delay) {
		_config.put(DELAY_TAG, delay);
	}
}
