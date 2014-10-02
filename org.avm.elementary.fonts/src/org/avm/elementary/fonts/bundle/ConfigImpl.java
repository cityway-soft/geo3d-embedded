package org.avm.elementary.fonts.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.fonts.FontsConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements FontsConfig {
	
	private String defaultFontsPath= "{0}/data/fonts/19700101010101";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
		// TODO Auto-generated constructor stub
	}

	public String getFontsPath() {
		if (_config.get(FONTSPATH_TAG) == null) {
			return defaultFontsPath;
		}
		return (String) _config.get(FONTSPATH_TAG);
	}

	public void setFontsPath(String path) {
		_config.put(FONTSPATH_TAG, path);
	}

}
