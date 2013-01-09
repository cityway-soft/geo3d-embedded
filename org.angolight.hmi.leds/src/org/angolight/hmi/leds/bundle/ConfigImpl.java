package org.angolight.hmi.leds.bundle;

import java.util.Enumeration;
import java.util.Properties;

import org.angolight.hmi.leds.Leds;
import org.angolight.hmi.leds.LedsConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements LedsConfig {


	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}
	
	public String getProperty(String key) {
		String text = (String) _config.get(key);
		return text;
	}

	public Properties getProperties() {
		Properties props = new Properties();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			String p = getProperty(key);
			if (p != null) {
				props.put(key, p);
			}
		}
		return props;
	}

	
	
	public void setStateList(String list){
		_config.put(STATE_LIST, list);
	}
	public String getStateList(){
		return (String)_config.get(STATE_LIST);
	}
	
	public void addState(String key, String value) {
		_config.put(key, value);
	}
	
	public void removeState(String state) {
		_config.remove(state);
	}
	
	public String getState(String key){
		return (String)_config.get(key);
	}

	public void addPriority(String key, String value) {
		_config.put(Leds.PRIORITY_ON_BO+key, value);
	}
	
	public String getPriorityEvents(String key){
		return (String)_config.get(Leds.PRIORITY_ON_BO+key);
	}

	public void removePriority(String bostate) {
		_config.remove(bostate);
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

}
