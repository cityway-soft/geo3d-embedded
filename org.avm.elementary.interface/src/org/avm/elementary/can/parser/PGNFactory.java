package org.avm.elementary.can.parser;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;

public abstract class PGNFactory extends BaseKeyedPoolableObjectFactory {
	public static java.util.Map _factories = new java.util.HashMap();

	protected abstract PGN makeObject() throws Exception;

	public Object makeObject(Object key) throws Exception {
		if (!_factories.containsKey(key)) {
			throw new ClassNotFoundException();
		}
		return ((PGNFactory) _factories.get(key)).makeObject();
	}
}
