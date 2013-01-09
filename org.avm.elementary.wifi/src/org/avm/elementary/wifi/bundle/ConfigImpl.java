package org.avm.elementary.wifi.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.wifi.WifiManagerConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements WifiManagerConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getBaliseList() {
		return (String) _config.get(TAG_BALISE_LIST);
	}

	public void setBaliseList(String list) {
		_config.put(TAG_BALISE_LIST, list);
	}

	public String getDisconnectTimeout() {
		return (String) _config.get(TAG_DISCONNECT_TIMEOUT);
	}

	public void setDisconnectTimeout(String timeout) {
		_config.put(TAG_DISCONNECT_TIMEOUT, timeout);
	}

}
