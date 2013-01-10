package org.avm.device.fm6000.sound.bundle;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.avm.device.fm6000.sound.SoundConfig;
import org.avm.device.sound.Sound;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements SoundConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public void add(Properties p) {
		String name = p.getProperty(Sound.NAME);
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
			Object key = (Object) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			Properties p = get((String)key);
			if (p != null) {
				d.put(key, p);
			}
		}
		return d;
	}

	public String toString() {
		StringBuffer text = new StringBuffer();
		for (Enumeration it = get().keys(); it.hasMoreElements();) {
			String name = (String) it.nextElement();
			Properties info = get(name);
			if (info != null) {
				text.append(info.toString() + "\n");
			}
		}
		return text.toString();
	}
}
