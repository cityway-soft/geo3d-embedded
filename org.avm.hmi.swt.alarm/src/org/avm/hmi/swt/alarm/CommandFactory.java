package org.avm.hmi.swt.alarm;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.component.ComponentContext;

public abstract class CommandFactory {

	public static Map factories = new HashMap();

	protected abstract Command createCommand(ComponentContext context,Properties properties);

	public static final Command createCommand(String clazz, ComponentContext context, Properties properties)
			throws ClassNotFoundException {
		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((CommandFactory) factories.get(clazz)).createCommand(context, properties);
	}

}
