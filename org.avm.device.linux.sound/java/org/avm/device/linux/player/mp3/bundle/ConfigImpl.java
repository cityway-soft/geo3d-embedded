package org.avm.device.linux.player.mp3.bundle;

import java.net.URL;
import java.util.Enumeration;

import org.avm.device.linux.player.mp3.PlayerConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.framework.Bundle;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements PlayerConfig {

	public static final Object PLAYER_TAG = "player";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected String getResourceFileName() {
		String result = "config.xml";
		Bundle bundle = _context.getBundleContext().getBundle();
		Enumeration e = bundle.findEntries("/", "config*.xml", true);
		while (e.hasMoreElements()) {
			URL url = (URL) e.nextElement();
			String suffix = "config-player-" + System.getProperty(PLATEFORM)
					+ ".xml";
			if (url.toExternalForm().endsWith(suffix)) {
				result = url.getFile();
				break;
			}
		}
		return result;
	}

	public String getPlayerCommand() {
		return (String) _config.get(PLAYER_TAG);
	}

	public void setPlayerCommand(String command) {
		_config.put(PLAYER_TAG, command);

	}

}
