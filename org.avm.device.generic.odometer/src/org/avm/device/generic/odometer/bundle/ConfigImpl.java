package org.avm.device.generic.odometer.bundle;

import java.util.Dictionary;

import org.avm.device.generic.odometer.OdometerConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements OdometerConfig {

	public static String COUNTER_FACTOR_TAG = "factor";

	public static Double DEFAULT_COUNTER_FACTOR = new Double(1d);

	public static String SPEED_LIMIT_TAG = "limit";

	public static Double DEFAULT_SPEED_LIMIT = new Double(1d);

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		result.put(COUNTER_FACTOR_TAG, DEFAULT_COUNTER_FACTOR);
		result.put(SPEED_LIMIT_TAG, DEFAULT_SPEED_LIMIT);
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public Double getCounterFactor() {
		return (Double) _config.get(COUNTER_FACTOR_TAG);
	}

	public void setCounterFactor(Double factor) {
		_config.put(COUNTER_FACTOR_TAG, factor);
	}

	public Double getSpeedLimit() {
		return (Double) _config.get(SPEED_LIMIT_TAG);
	}

	public void setSpeedLimit(Double limit) {
		_config.put(SPEED_LIMIT_TAG, limit);
	}

}
