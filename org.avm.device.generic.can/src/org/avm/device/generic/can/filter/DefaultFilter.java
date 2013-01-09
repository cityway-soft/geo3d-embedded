package org.avm.device.generic.can.filter;

import org.avm.elementary.can.parser.CANParser;

public class DefaultFilter implements Filter {
	
	private CANParser _parser;

	public DefaultFilter(CANParser parser) {
		_parser = parser;	
	}

	public boolean execute(byte[] pgn) {
		return true;
	}

	public static class DefaultFilterFactory extends FilterFactory {
		protected Filter create(CANParser parser) throws Exception {
			return new DefaultFilter(parser);
		}
	}

	static {
		FilterFactory._factories.put(DefaultFilter.class.getName(),
				new DefaultFilterFactory());
	}
}
