package org.avm.device.knet.gps.bundle;

import java.util.Dictionary;

import org.avm.device.knet.gps.GpsConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements GpsConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		// result.put(DELAY_TAG, DEFAULT_DELAY);
		// result.put(CORRECT_TAG, DEFAULT_CORRECT);
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public Boolean getCorrect() {
		return (Boolean) _config.get(CORRECT_TAG);
	}

	public void setCorrect(Boolean correct) {
		_config.put(CORRECT_TAG, correct);
	}

	public Double getDelay() {
		return (Double) _config.get(DELAY_TAG);
	}

	public void setDelay(Double delay) {
		_config.put(DELAY_TAG, delay);
	}
}
