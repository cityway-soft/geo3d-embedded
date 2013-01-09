package org.avm.device.linux.odometer.bundle;

import org.avm.device.linux.odometer.OdometerConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements OdometerConfig {

	public static String COUNTER_FACTOR_TAG = "factor";
	public static String SPEED_LIMIT_TAG = "speed";
	public static String TRACK_LIMIT_TAG = "track";
	public static String SAMPLE_LIMIT_TAG = "sample";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public double getCounterFactor() {
		return ((Double) _config.get(COUNTER_FACTOR_TAG)).doubleValue();
	}

	public void setCounterFactor(double factor) {
		_config.put(COUNTER_FACTOR_TAG, new Double(factor));
	}

	public double getSpeedLimit() {
		return ((Double) _config.get(SPEED_LIMIT_TAG)).doubleValue();
	}

	public void setSpeedLimit(double limit) {
		_config.put(SPEED_LIMIT_TAG, new Double(limit));
	}

	public int getSampleLimit() {
		return ((Integer) _config.get(SAMPLE_LIMIT_TAG)).intValue();
	}

	public void setSampleLimit(int sample) {
		_config.put(SAMPLE_LIMIT_TAG, new Integer(sample));		
	}
	
	public double getTrackLimit() {
		return ((Double) _config.get(TRACK_LIMIT_TAG)).doubleValue();
	}

	public void setTrackLimit(double track) {
		_config.put(TRACK_LIMIT_TAG, new Double(track));
	}

}
