package org.avm.business.recorder;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Didier LALLEMAND
 * 
 */
public abstract class ActionFactory {

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
		String clazz = object.getClass().getName();
		Vector v = (Vector) factories.get(clazz);
		if (v != null) {
			for (int i = 0; i < v.size(); i++) {
				Action action = (Action) v.elementAt(i);
				action.compute(object);
			}
		}
	}

	public static final void inject(Object object) {
		Iterator iter = factories.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Vector v = (Vector) factories.get(key);
			for (int i = 0; i < v.size(); i++) {
				Action action = (Action) v.elementAt(i);
				action.configure(object);
			}
		}
	}

}
