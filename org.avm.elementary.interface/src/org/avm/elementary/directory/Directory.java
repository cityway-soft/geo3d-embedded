package org.avm.elementary.directory;

import java.util.Properties;

public interface Directory {
	public static final String KEY = "org.avm.elementary.directory.key";

	public Properties getProperty(String key);
}
