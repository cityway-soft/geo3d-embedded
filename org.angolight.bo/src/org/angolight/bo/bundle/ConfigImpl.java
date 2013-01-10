package org.angolight.bo.bundle;

import java.io.IOException;

import org.angolight.bo.impl.BoConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements BoConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}


	public double getVMinDown() {
		Double vmin = (Double) _config.get(VMIN_DOWN_TAG);
		if (vmin == null)
			return DEFAULT_VMIN_DOWN;
		else
			return vmin.doubleValue();
	}

	public double getVMinUp() {
		Double vmin = (Double) _config.get(VMIN_UP_TAG);
		if (vmin == null)
			return DEFAULT_VMIN_UP;
		else
			return vmin.doubleValue();
	}

	public double getVMaxDown() {
		Double vmax = (Double) _config.get(VMAX_DOWN_TAG);
		if (vmax == null)
			return DEFAULT_VMAX_DOWN;
		else
			return vmax.doubleValue();
	}

	public double getVMaxUp() {
		Double vmax = (Double) _config.get(VMAX_UP_TAG);
		if (vmax == null)
			return DEFAULT_VMAX_UP;
		else
			return vmax.doubleValue();
	}

	public void setVMax(Double vmax_up, Double vmax_down) {
		_config.put(VMAX_UP_TAG, vmax_up);
		_config.put(VMAX_DOWN_TAG, vmax_down);
	}

	public void setVMin(Double vmin_up, Double vmin_down) {
		_config.put(VMIN_UP_TAG, vmin_up);
		_config.put(VMIN_DOWN_TAG, vmin_down);
	}

	public double getTriggerPercent() {
		Double trigPercent = (Double) _config.get(TRIGGER_PERCENT_TAG);

		if (trigPercent == null)
			return DEFAULT_TRIGGER_PERCENT;
		else
			return trigPercent.doubleValue();
	}

	public void setTriggerPercent(Double trigPercent) {
		_config.put(TRIGGER_PERCENT_TAG, trigPercent);
	}

	public String getCurvesFileName() {
		String filename = (String) _config.get(CURVES_FILE_NAME_TAG);

		if (filename == null)
			return DEFAULT_CURVES_FILE_NAME;
		else
			return filename;
	}

	public void setCurvesFileName(String filename) throws IOException {
		_config.put(CURVES_FILE_NAME_TAG, filename);
	}
}
