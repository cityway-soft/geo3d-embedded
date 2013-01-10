package org.avm.business.vocal.bundle;

import java.util.Enumeration;
import java.util.Properties;

import org.avm.business.vocal.VocalConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements VocalConfig {

	public static final String FILENAME_TAG = "filename";
	public static final String SLEEP_TAG = "sleep-before-play";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getFileName() {
		return (String) _config.get(FILENAME_TAG);
	}

	public void setFileName(String fileName) {
		_config.put(FILENAME_TAG, fileName);
	}

	public long getSleepBeforePlay() {
		String time = (String) _config.get(SLEEP_TAG);
		if (time == null){
			time="0";
		}
		return Long.parseLong( time  );
	}

	public void setSleepBeforePlay(long time) {
		_config.put(SLEEP_TAG, Long.toString(time));
	}
	
	public void addProperty(String key, Properties p) {
		String text = save(p);
		_config.put(key, text);
	}

	public Properties getProperty(String key) {
		if (key == null) {
			return getProperties();
		} else {
			String text = (String) _config.get(key);
			return load(text);
		}
	}
	
	public void removeProperty(String key) {
		_config.remove(key);
	}

	private Properties getProperties() {
		Properties props = new Properties();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			Properties p = getProperty(key);
			if (p != null) {
				props.put(key, p);
			}
		}
		return props;
	}

}
