package org.avm.elementary.can.logger.bundle;

import org.avm.elementary.can.logger.LoggerConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements LoggerConfig {

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
		return (String) _config.get(FILENAME_TAG);
	}

	public void setFilename(String filename) {
		_config.put(FILENAME_TAG, filename);
	}
}
