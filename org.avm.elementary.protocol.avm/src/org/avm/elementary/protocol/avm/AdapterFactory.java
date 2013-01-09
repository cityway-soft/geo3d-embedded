package org.avm.elementary.protocol.avm;

import java.util.HashMap;
import java.util.Map;

public abstract class AdapterFactory {

	public static Map factories = new HashMap();

	protected abstract Object adapt(Object o, Class clazz);

	public static final Object create(Object o, Class clazz)
			throws ClassNotFoundException {

		String name = o.getClass().getName();

		if (!factories.containsKey(name)) {
			StringBuffer sb = new StringBuffer();
			int index = name.lastIndexOf('.');
			if (index >= 0) {
				sb.append(name.substring(0, index));
				sb.append(".adapter");
				sb.append(name.substring(index));
				sb.append("Adapter");
			} else {
				sb.append("adapter.");
				sb.append(name);
				sb.append("Adapter");
			}

			String className = sb.toString();
			Class.forName(className);

			if (!factories.containsKey(name))
				throw new ClassNotFoundException(className);
		}
		return ((AdapterFactory) factories.get(name)).adapt(o, clazz);
	}
}
