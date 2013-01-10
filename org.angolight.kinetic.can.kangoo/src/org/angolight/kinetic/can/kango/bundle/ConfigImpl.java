package org.angolight.kinetic.can.kango.bundle;

import org.angolight.kinetic.can.kangoo.KineticConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements KineticConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public double getAcquisitionPeriod() {
		return ((Double) _config.get(ACQUISITION_PERIOD_TAG)).doubleValue();
	}

	public int getMedianFilterLength() {
		return ((Integer) _config.get(MEDIAN_FILTER_LENGTH_TAG)).intValue();
	}

	public double getNotificationPeriod() {
		return ((Double) _config.get(NOTIFICATION_PERIOD_TAG)).doubleValue();
	}

	public void setAcquisitionPeriod(double period) {
		_config.put(ACQUISITION_PERIOD_TAG, new Double(period));
	}

	public void setMedianFilterLength(int length) {
		_config.put(MEDIAN_FILTER_LENGTH_TAG, new Integer(length));
	}

	public void setNotificationPeriod(double period) {
		_config.put(NOTIFICATION_PERIOD_TAG, new Double(period));
	}

	public String getCanServicePid() {
		return (String) _config.get(CAN_SERVICE_PID_TAG);
	}

	public void setCanServicePid(String pid) {
		_config.put(CAN_SERVICE_PID_TAG, pid);		
	}

}
