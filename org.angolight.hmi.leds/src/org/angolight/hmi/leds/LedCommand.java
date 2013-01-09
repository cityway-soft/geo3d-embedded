package org.angolight.hmi.leds;

import java.util.StringTokenizer;

import org.angolight.device.leds.Leds;

public class LedCommand {
	private static final int M = 0;
	private static final int X = 1;

	private int _type;
	private short _command;
	private byte _period;
	private byte _cycle;
	
	public static LedCommand getInstance(String cmd, SequenceManager sequenceManager) {
		LedCommand result = null;
		StringTokenizer t = new StringTokenizer(cmd, ".");
		String stype = ((String) t.nextElement()).toUpperCase().trim();
		String scommand = ((String) t.nextElement()).toUpperCase();
		byte period = 0;

		if (t.hasMoreElements()) {
			String speriod = ((String) t.nextElement()).toUpperCase();
			period = Byte.parseByte(speriod);
		}
		if (stype.equals("X")) {
			byte cycle = 0;
			if (t.hasMoreElements()) {
				String scycle = ((String) t.nextElement())
						.toUpperCase();
				cycle = Byte.parseByte(scycle);
			}

			byte id = Byte.parseByte(scommand);
			byte address = sequenceManager.getSequenceAddress("" + id);
			result = new LedCommand(X, address, cycle, period);
		} else {
			short command = Short.parseShort(scommand, 16);
			result = new LedCommand(M, command, period);
		}

		return result;

	}

	public int execute(Leds leds) {
		int result=-1;
		if (_type == X) {
			result=leds
					.X(/* address */(byte) _command,  _cycle,_period,
							true);
		} else {
				result=leds.M(_command, _period, true);
		}
		return result;
	}

	private LedCommand(int type, short command, byte cycle, byte period) {
		_type = type;
		_command = command;
		_cycle = cycle;
		_period = period;
	}

	private LedCommand(int type, short command, byte period) {
		this(type, command, (byte) 0, period);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(_type == X ? "X" : "M");
		buf.append(" command=");
		buf.append(_command);
		buf.append(" period=");
		buf.append(_period);
		buf.append(" cycle=");
		buf.append(_cycle);
		return buf.toString();
	}
}

