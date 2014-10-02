package org.avm.elementary.fonts;

import org.avm.elementary.common.Config;

public interface FontsConfig extends Config {
	final static String FONTSPATH_TAG = "fonts-path";

	String getFontsPath();

	void setFontsPath(String path);

}
