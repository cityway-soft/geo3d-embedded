package org.avm.elementary.dnssd.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.dnssd.DnsSdServiceConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements DnsSdServiceConfig {

	public static final String ADDRESS_TAG = "address";

	public static final String PORT_TAG = "port";

	public static final String PERIOD_TAG = "period";

		

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	

	public Integer getPort() {
		return (Integer) _config.get(PORT_TAG);
	}

	public void setPort(Integer port) {
		_config.put(PORT_TAG, port);
	}

	public String getAddress() {
		return (String) _config.get(ADDRESS_TAG);
	}

	public void setAddress(String address) {
		_config.put(ADDRESS_TAG, address);
	}

	public Integer getPeriod() {
		return (Integer) _config.get(PERIOD_TAG);
	}

	public void setPeriod(Integer period) {
		_config.put(PERIOD_TAG, period);
	}
}
