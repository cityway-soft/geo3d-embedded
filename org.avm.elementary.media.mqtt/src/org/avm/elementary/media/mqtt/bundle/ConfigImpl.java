package org.avm.elementary.media.mqtt.bundle;

import java.util.StringTokenizer;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.media.mqtt.MediaMqttConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements MediaMqttConfig {

	public static final String ADDRESS_TAG = "address";

	public static final String PORT_TAG = "port";

	public static final String PERIOD_TAG = "period";

	private static final String TERMINAL_ID_KEY = "org.avm.terminal.id";
		

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getMediaId() {
		String result = null;
		
		result = getProperty(TERMINAL_ID_KEY, "FF:00:00:00:00:00:00");
		if (result == null){
			result = getProperty("org.avm.plateform.id", "FF:00:00:00:00:00:00");//-- deprecated
		}
		StringTokenizer t = new StringTokenizer(result, ":");
		StringBuffer buf=new StringBuffer();
		while (t.hasMoreElements()){
			buf.append(t.nextElement());
		}
		result=buf.toString();
		return result;
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
