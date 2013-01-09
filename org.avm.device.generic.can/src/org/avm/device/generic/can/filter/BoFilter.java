package org.avm.device.generic.can.filter;

import org.avm.elementary.can.parser.CANParser;

public class BoFilter implements Filter {

	public static int FMS_SPEED_FREQUENCY_DIV = 8;
	public static int FMS_DISTANCE_FREQUENCY_DIV = 8;

	private int _fmsSpeedFilter = FMS_SPEED_FREQUENCY_DIV;
	private int _fmsDistanceFilter = FMS_DISTANCE_FREQUENCY_DIV;

	private CANParser _parser;

	public BoFilter(CANParser parser) {
		_parser = parser;
	}

	public boolean execute(byte[] buffer) {

		int pgnID = _parser.getPgnId(buffer);

		switch (pgnID) {
		case 0x489:
		case 0x490:
			return true;

		case 0xFEF1: {
			_fmsSpeedFilter--;

			if (_fmsSpeedFilter == 0) {
				_fmsSpeedFilter = FMS_SPEED_FREQUENCY_DIV;

				return true;
			} else
				return false;
		}

		case 0xFF55: {
			_fmsDistanceFilter--;

			if (_fmsDistanceFilter == 0) {
				_fmsDistanceFilter = FMS_DISTANCE_FREQUENCY_DIV;

				return true;
			} else
				return false;
		}

		default:
			return false;
		}
	}

	public static class DefaultFilterFactory extends FilterFactory {
		protected Filter create(CANParser parser) throws Exception {
			return new BoFilter(parser);
		}
	}

	static {
		FilterFactory._factories.put(BoFilter.class.getName(),
				new DefaultFilterFactory());
	}

}
