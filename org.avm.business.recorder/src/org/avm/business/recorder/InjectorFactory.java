package org.avm.business.recorder;

import java.util.Vector;

/**
 * @author Didier LALLEMAND
 * 
 */
public abstract class InjectorFactory {

	public static java.util.Map factories = new java.util.HashMap();

	public static void addAction(Class clazz, Action process) {
		Vector v = (Vector) factories.get(clazz.getName());
		if (v == null) {
			v = new Vector();
			factories.put(clazz.getName(), v);
		}
		v.add(process);
	}

	public static final void compute(Object object) {
		Vector v = (Vector) factories.get(object.getClass().getName());
		for (int i = 0; i < v.size(); i++) {
			Action action = (Action) v.elementAt(i);
			action.compute(object);
		}
	}

}
