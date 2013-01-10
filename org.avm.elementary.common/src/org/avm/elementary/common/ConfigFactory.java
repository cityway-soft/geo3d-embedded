package org.avm.elementary.common;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.cm.ConfigurationAdmin;

public abstract class ConfigFactory {

	public static Map factories = new HashMap();

	protected abstract Config create(ConfigurationAdmin cm);

	public static final Config create(String id, ConfigurationAdmin cm)
			throws ClassNotFoundException {
		if (!factories.containsKey(id)) {

			Class.forName(id);

			if (!factories.containsKey(id))
				throw new ClassNotFoundException(id);
		}
		return ((ConfigFactory) factories.get(id)).create(cm);
	}
}
