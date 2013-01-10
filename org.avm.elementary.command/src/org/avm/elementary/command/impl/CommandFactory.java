package org.avm.elementary.command.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;

public abstract class CommandFactory {

	public static Map factories = new HashMap();

	protected abstract Command createCommand();

	public static final Command createCommand(String clazz)
			throws ClassNotFoundException {
		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((CommandFactory) factories.get(clazz)).createCommand();
	}

}
