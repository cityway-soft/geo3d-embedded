package org.avm.business.girouette.bundle;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.avm.business.girouette.GirouetteConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements GirouetteConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public void addProperty(String key, Properties p) {
		String text = save(p);
		_config.put(key, text);
	}

	public Properties getProperty(String key) {
		String text = (String) _config.get(key);
		if (text == null)
			return null;
		return load(text);
	}

	public void removeProperty(String key) {
		_config.remove(key);
	}

	public Dictionary getProperties() {
		Hashtable d = new Hashtable();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			Properties p = getProperty(key);
			if (p != null) {
				d.put(key, p);
			}
		}
		return d;
	}




}
