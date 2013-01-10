package org.avm.elementary.time.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.time.TimeManagerConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements TimeManagerConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public int getFrequency() {
		return ((Integer)_config.get(TAG_FREQUENCY)).intValue();
	}

	public void setFrequency(int freq) {
		_config.put(TAG_FREQUENCY, new Integer(freq));
	}

	public boolean isPositionChecked() {
		return ((String)_config.get(TAG_CHECKPOS)).equals("true");
	}

	public void setPositionCheck(boolean b) {
		_config.put(TAG_CHECKPOS, String.valueOf(b));
	}

}
