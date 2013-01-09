package org.avm.elementary.can.parser.kangoo;

import java.text.DecimalFormat;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.avm.elementary.can.parser.CANParser;
import org.avm.elementary.can.parser.PGN;

public class PGNTest extends TestCase {

	private CANParser parser;
	private DecimalFormat df = new DecimalFormat(".###");

	public PGNTest() {
		super();
		parser = new Activator();
	}

	protected void testPGN(PGN expected) throws Exception {
		byte[] buffer = new byte[14];
		parser.put(expected, buffer);
		PGN actual = parser.get(buffer);
		try {
			Assert.assertEquals(expected, actual);
		} catch (Throwable e) {
			System.out.println("[DSU] " + toHexaString(buffer));
			System.out.println(expected + "\n" + actual
					+ "\n");
			throw new Exception(e.getMessage());
		}
	}
	
	public static String toHexaString(byte[] data) {
		byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	public void testPGN_102() throws Exception {
		PGN_102 pgn = new PGN_102();
		long v = 0;
		v = (long) ((((655.34 - 0.0) * Math.random() + 0.0)- 0.0) / 0.01);
		pgn.vehiclespeed.value = (0.01 * v + 0.0);
		testPGN(pgn);
	}


	
}

