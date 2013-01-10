package org.angolight.halfcycle.bundle;

import org.angolight.halfcycle.impl.HalfCycleConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements HalfCycleConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public double getMinimumSpeedDown() {
		return ((Double) _config.get(MINIMUM_SPEED_DOWN_TAG)).doubleValue();
	}

	public double getMinimumSpeedUp() {
		return ((Double) _config.get(MINIMUM_SPEED_UP_TAG)).doubleValue();
	}

	public double getNegativeAccelerationDown() {
		return ((Double) _config.get(NEGATIVE_ACCELERATION_DOWN_TAG))
				.doubleValue();
	}

	public double getNegativeAccelerationUp() {
		return ((Double) _config.get(NEGATIVE_ACCELERATION_UP_TAG))
				.doubleValue();
	}

	public double getPositiveAccelerationDown() {
		return ((Double) _config.get(POSITIVE_ACCELERATION_DOWN_TAG))
				.doubleValue();
	}

	public double getPositiveAccelerationUp() {
		return ((Double) _config.get(POSITIVE_ACCELERATION_UP_TAG))
				.doubleValue();
	}

	public void setMinimumSpeedDown(double speed) {
		_config.put(MINIMUM_SPEED_DOWN_TAG, new Double(speed));
	}

	public void setMinimumSpeedUp(double speed) {
		_config.put(MINIMUM_SPEED_UP_TAG, new Double(speed));
	}

	public void setNegativeAccelerationDown(double acceleration) {
		_config.put(NEGATIVE_ACCELERATION_DOWN_TAG, new Double(acceleration));
	}

	public void setNegativeAccelerationUp(double acceleration) {
		_config.put(NEGATIVE_ACCELERATION_UP_TAG, new Double(acceleration));
	}

	public void setPositiveAccelerationDown(double acceleration) {
		_config.put(POSITIVE_ACCELERATION_DOWN_TAG, new Double(acceleration));
	}

	public void setPositiveAccelerationUp(double acceleration) {
		_config.put(POSITIVE_ACCELERATION_UP_TAG, new Double(acceleration));
	}

	public String getFilename() {
		return ((String) _config.get(FILENAME_TAG));
	}

	public void setFilename(String filename) {
		_config.put(FILENAME_TAG, filename);
	}

}
