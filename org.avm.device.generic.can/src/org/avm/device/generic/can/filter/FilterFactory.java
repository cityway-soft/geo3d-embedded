package org.avm.device.generic.can.filter;

import java.util.HashMap;
import java.util.Map;

import org.avm.elementary.can.parser.CANParser;

public abstract class FilterFactory {

	public static Map _factories = new HashMap();

	protected abstract Filter create(CANParser parser) throws Exception;

	public static Filter create(String className, CANParser parser) throws Exception {
		if (!_factories.containsKey(className)) {
			Class.forName(className);
		}

		return ((FilterFactory) _factories.get(className)).create(parser);
	}

}