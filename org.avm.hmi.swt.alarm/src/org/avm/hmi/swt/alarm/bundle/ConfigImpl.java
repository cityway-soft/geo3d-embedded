package org.avm.hmi.swt.alarm.bundle;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.common.AbstractConfig;
import org.avm.hmi.swt.alarm.AlarmConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements AlarmConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		return result;
	}

	protected String getPid() {
		return Activator.getDefault().getPid();
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

	public String toString() {
		StringBuffer text = new StringBuffer();

		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			Properties p = getProperty(key);
			if (p != null) {
				text.append(p.toString() + "\n");
			}
		}
		return text.toString();
	}

}
