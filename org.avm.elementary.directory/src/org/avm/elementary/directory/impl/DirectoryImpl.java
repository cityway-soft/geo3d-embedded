package org.avm.elementary.directory.impl;

import java.util.Properties;

import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.directory.Directory;

public class DirectoryImpl implements Directory, ConfigurableService {

	DirectoryConfig _config;

	public Properties getProperty(final String key) {
		return _config.getProperty(key);
	}

	public void configure(final Config config) {
		_config = (DirectoryConfig) config;
		if (_config !=null && !_config.loadProperties(null)){
			_config.createDefaultProperties();
		}
		
	}

}
