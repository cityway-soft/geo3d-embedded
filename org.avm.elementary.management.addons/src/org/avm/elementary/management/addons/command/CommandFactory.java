package org.avm.elementary.management.addons.command;

import org.avm.elementary.management.addons.Command;

/**
 * @author Didier LALLEMAND
 * 
 */
public abstract class CommandFactory {

	public static final float VERSION = (float) 1.00;

	private static final String NAME = "basic";

	public static java.util.Map factories = new java.util.HashMap();

	public static final Command create(String name)
			throws ClassNotFoundException {
		String classname = name.substring(0, 1).toUpperCase()
				+ name.substring(1).toLowerCase();

		Package pack = CommandFactory.class.getPackage();
		String spack = pack.getName();

		String clazz = spack + "." + classname + "Command";

		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((CommandFactory) factories.get(clazz)).create();
	}

	protected abstract Command create();

	public float getVersion() {
		return VERSION;
	}

	public String getName() {
		return NAME;
	}

}
