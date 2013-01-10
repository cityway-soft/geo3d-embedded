package org.avm.business.tft.bundle;

import org.avm.business.tft.TftConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements TftConfig {

	private String _defaultFontSize = "8.5cm";

	private String _defaultFont = "Context Ultra Condensed SSi";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public void setFontSize(String size) {
		_config.put(FONTSIZE_TAG, size);
	}

	public void setFontName(String name) {
		_config.put(FONTNAME_TAG, name);
	}

	public String getFontName() {
		if (_config.get(FONTNAME_TAG) == null) {
			return _defaultFont;
		}
		return (String) _config.get(FONTNAME_TAG);
	}

	public String getFontSize() {
		if (_config.get(FONTSIZE_TAG) == null) {
			return _defaultFontSize;
		}
		return (String) _config.get(FONTSIZE_TAG);
	}
}
