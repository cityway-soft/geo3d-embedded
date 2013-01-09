package org.avm.device.knet.sensor.bundle;

import java.util.Dictionary;

import org.avm.device.knet.sensor.SensorConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements SensorConfig {
	public static String PORTE_AV_ID_TAG = "id.ref";
	public static String DEFAULT_PORTE_AV_ID = "6";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		// result.put(PORTE_AV_ID_TAG, DEFAULT_PORTE_AV_ID);
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public void setIdPorteAv(String id) {
		_config.put(PORTE_AV_ID_TAG, id);
	}

	public String getIdPorteAv() {
		return (String) _config.get(PORTE_AV_ID_TAG);
	}
}
