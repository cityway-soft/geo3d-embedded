package org.angolight.hmi.swt.leds.bundle;

import java.util.Properties;

import org.angolight.hmi.swt.leds.LedsConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements LedsConfig {




	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getUrlConnection() {
		return (String) _config.get(URL_CONNECTION_TAG);
	}

	public void setUrlConnection(String uri) {
		_config.put(URL_CONNECTION_TAG, uri);
	}

	public Integer getBrightness() {
		return (Integer) _config.get(BRIGHTNESS_TAG);
	}

	public void setBrightness(Integer value) {
		_config.put(BRIGHTNESS_TAG, value);
	}

	public Properties getSequences() {
		String text = (String) _config.get(SEQUENCES_TAG);
		if (text == null) {
			text = save(new Properties());
			_config.put(SEQUENCES_TAG, text);
			updateConfig(false);
		}
		return load(text);
	}

	public void setSequences(Properties p) {
		String text = save(p);
		_config.put(SEQUENCES_TAG, text);
	}

	public boolean isInside() {
		String res = (String) _config.get(INSIDE_TAG);
		return (res!=null && res.equalsIgnoreCase("true"));
	}
	
	public void setInside(boolean b) {
		_config.put(INSIDE_TAG, String.valueOf(b));
	}

	public boolean isOval() {
		String res = (String) _config.get(OVAL_TAG);
		return (res!=null && res.equalsIgnoreCase("true"));
	}

	public void setOval(boolean b) {
		_config.put(OVAL_TAG, String.valueOf(b));
	}
}
