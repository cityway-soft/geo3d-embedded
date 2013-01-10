package org.avm.hmi.swt.management;

import org.eclipse.swt.widgets.Composite;

public abstract class PanelFactory {

	public static java.util.Map factories = new java.util.HashMap();

	public static final AbstractPanel create(String clazz,Composite parent, int style)
			throws ClassNotFoundException {
	
		if (!factories.containsKey(clazz)) {
			Class.forName(clazz);
			if (!factories.containsKey(clazz))
				throw new ClassNotFoundException(clazz);
		}
		return ((PanelFactory) factories.get(clazz)).create(parent, style);
	}

	protected abstract AbstractPanel create(Composite parent, int style);


}
