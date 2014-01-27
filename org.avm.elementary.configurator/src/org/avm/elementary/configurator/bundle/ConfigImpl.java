package org.avm.elementary.configurator.bundle;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig {
	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public void add(Properties p) {
		String text = save(p);
	}

	public void remove(String index) {
		_config.remove(index);
	}

	public Properties get(String name) {
		String text = (String) _config.get(name);
		return load(text);
	}

	public Dictionary get() {
		Dictionary d = new Hashtable();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			Properties p = get(key);
			if (p != null) {
				d.put(key, p);
			}
		}
		return d;
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
			Properties info = get(key);
			if (info != null) {
				text.append(key + ": " + info.toString() + "\n");
			}
		}
		return text.toString();
	}
}
