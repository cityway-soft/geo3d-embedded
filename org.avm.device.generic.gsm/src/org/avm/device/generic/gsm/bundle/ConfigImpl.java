package org.avm.device.generic.gsm.bundle;

import org.avm.device.generic.gsm.GsmConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements GsmConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getUrlConnection() {
		return (String) _config.get(URL_CONNECTION_TAG);
	}

	public void setUrlConnection(String uri) {
		_config.put(URL_CONNECTION_TAG, uri);
	}
}
