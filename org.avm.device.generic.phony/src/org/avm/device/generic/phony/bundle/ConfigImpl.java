package org.avm.device.generic.phony.bundle;

import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.device.generic.phony.impl.PhonyConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements PhonyConfig {

	private static int MAX_RING_LEVEL_VOLUME = 0;
	private static int MAX_LEVEL_VOLUME = 0;

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public int getDefaultSoundVolume() {
		Integer volume = (Integer) _config.get(TAG_SOUND_VOLUME);
		if (volume == null) {
			volume = new Integer(0);
		}
		return volume.intValue();
	}

	public void setDefaultSoundVolume(int volume) {
		Integer i = new Integer(volume);
		_config.put(TAG_SOUND_VOLUME, i);
	}

	public void addSpecificAtCommand(String id, String request, String desc) {
		Properties p = new Properties();
		p.put(PhonyConfig.TAG_CMD_REQUEST, request);
		p.put(PhonyConfig.TAG_CMD_DESC, desc);
		String text = save(p);
		_config.put(TAG_AT + id, text);
	}

	public void removeSpecificAtCommand(String id) {
		_config.remove(TAG_AT + id);
	}

	public Properties getSpecificCommandProperties(String key) {
		if (key == null) {
			return getSpecificCommands();
		} else {
			String tag = key.startsWith(TAG_AT) ? key : TAG_AT + key;
			String text = (String) _config.get(tag);
			if (text == null) {
				_log.warn(TAG_AT + key + " not found!");
				return null;
			}
			return load(text);
		}
	}

	public String getSpecificCommand(String name) {
		Properties p = getSpecificCommandProperties(name);
		if (p==null){
			return null;
		}
		return p.getProperty(TAG_CMD_REQUEST);
	}

	private Properties getSpecificCommands() {
		Properties props = new Properties();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if (!key.startsWith(TAG_AT))
				continue;
			Properties p = getSpecificCommandProperties(key);
			if (p != null) {
				props.put(key, p);
			}
		}
		return props;
	}

	public String[] getInitAtCommand() {
		String list = (String) _config.get(TAG_INIT);

		StringTokenizer t = new StringTokenizer(list, ",");
		String[] result = new String[t.countTokens()];
		int i = 0;
		while (t.hasMoreElements()) {
			String s = (String) t.nextElement();
			result[i] = s;
			i++;
		}
		return result;
	}

	public void setInitAtCommand(String[] list) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < list.length; i++) {
			buf.append(list[i]);
			if (i != list.length - 1) {
				buf.append(",");
			}
		}
		_config.put(TAG_INIT, buf.toString());
	}

	public int getMaxLevelVolume() {
		if (MAX_LEVEL_VOLUME == 0) {
			int count = 0;
			for (Enumeration it = _config.keys(); it.hasMoreElements();) {
				String key = (String) it.nextElement();
				if (key.startsWith(TAG_AT + "volume")) {
					count++;
				}
			}
			MAX_LEVEL_VOLUME = count;
		}
		return MAX_LEVEL_VOLUME;
	}

	

}
