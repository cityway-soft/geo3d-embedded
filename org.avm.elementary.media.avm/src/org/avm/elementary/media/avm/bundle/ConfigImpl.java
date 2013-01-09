package org.avm.elementary.media.avm.bundle;

import java.text.MessageFormat;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.media.avm.MediaAvmConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements MediaAvmConfig {

	public static final String ADDRESS_TAG = "address";

	public static final String PORT_TAG = "port";

	public static final String PERIOD_TAG = "period";

	private static final String VEHICULE_ID_KEY = "org.avm.vehicule.id";
	
	private static final String EXPLOITATION_ID_KEY = "org.avm.exploitation.id";
	

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getMediaId() {
		String result = null;
		Integer owner = new Integer(getProperty(EXPLOITATION_ID_KEY, "0"));
		Integer id = new Integer(getProperty(VEHICULE_ID_KEY, "0"));
		Object[] args = { owner, id };
		result = MessageFormat.format("AVM_{0,number,000}{1,number,00000}", args);
		System.out.println("ConfigImpl.getMediaId() : " + owner +" "+ id  +" "+ result);
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
