package org.avm.business.tracking.bundle;

import org.avm.business.tracking.TrackingConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements TrackingConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public void setFrequency(int freq) {
		_config.put(FREQUENCY_TAG, Integer.toString(freq));
	}

	public int getFrequency() {
		Object obj = _config.get(FREQUENCY_TAG);
		if (obj != null) {
			String val = obj.toString();
			return Integer.parseInt(val);
		}
		return 20;
	}

}
