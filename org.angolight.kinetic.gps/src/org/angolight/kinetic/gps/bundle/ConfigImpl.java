package org.angolight.kinetic.gps.bundle;

import org.angolight.kinetic.gps.KineticConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements KineticConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public double getMinimumSpeedDown() {
		return ((Double) _config.get(MINIMUM_SPEED_DOWN_TAG)).doubleValue();
	}

	public double getMinimumSpeedUp() {
		return ((Double) _config.get(MINIMUM_SPEED_UP_TAG)).doubleValue();
	}

	public void setMinimumSpeedDown(double speed) {
		_config.put(MINIMUM_SPEED_DOWN_TAG, new Double(speed));
	}

	public void setMinimumSpeedUp(double speed) {
		_config.put(MINIMUM_SPEED_UP_TAG, new Double(speed));
	}

}
