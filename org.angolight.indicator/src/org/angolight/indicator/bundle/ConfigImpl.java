package org.angolight.indicator.bundle;

import java.util.Properties;

import org.angolight.indicator.impl.IndicatorConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements IndicatorConfig {

	private static final Object PROPERTIES_TAG = "config";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getCanServicePid() {
		return (String) _config.get(CAN_SERVICE_PID_TAG);
	}

	public void setCanServicePid(String pid) {
		_config.put(CAN_SERVICE_PID_TAG, pid);
	}

	public String getFilename() {
		return ((String) _config.get(FILENAME_TAG));
	}

	public void setFilename(String filename) {
		_config.put(FILENAME_TAG, filename);
	}

	public Properties getProperties() {
		String text = (String) _config.get(PROPERTIES_TAG);
		if (text == null) {
			text = save(new Properties());
			_config.put(PROPERTIES_TAG, text);
			updateConfig(false);
		}
		return load(text);
	}

	public void setProperties(Properties p) {
		String text = save(p);
		_config.put(PROPERTIES_TAG, text);
	}

}
