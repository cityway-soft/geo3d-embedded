package org.avm.device.generic.can.manager.bundle;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.avm.device.generic.can.manager.CanManagerConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements CanManagerConfig, Constants {
	
	
	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}
	
	protected String getResourceFileName() {	
		return "config-manager.xml";
	}
	
	public void add(Properties p) {
		String name = p.getProperty(SERVICE_PID);
		String text = save(p);
		_config.put(name, text);
	}

	public void remove(String name) {
		_config.remove(name);
	}

	public Properties get(String name) {
		String text = (String) _config.get(name);
		return load(text);
	}
	

	public Dictionary get() {
		Hashtable d = new Hashtable();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			Object o = it.nextElement();
			if (o instanceof String) {
				String key = (String) o;
				if ("service.pid".equals(key) 
						|| "config.date".equals(key)
						|| "service.bundleLocation".equals(key)
						|| "org.avm.config.version".equals(key))

					continue;
				Properties p = get(key);
				if (p != null) {
					d.put(key, p);
				}
			}
		}
		return d;
	}
	
	public String toString() {
		StringBuffer text = new StringBuffer();

		for (Enumeration it = get().keys(); it.hasMoreElements();) {
			Object o = it.nextElement();
			if (o instanceof String) {
				String key = (String) o;
				Properties info = get(key);
				if (info != null) {
					text.append(info.toString() + "\n");
				}
			}
		}
		return text.toString();
	}
	
	
}
