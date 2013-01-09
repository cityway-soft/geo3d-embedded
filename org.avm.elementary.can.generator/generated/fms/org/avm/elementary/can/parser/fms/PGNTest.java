package org.avm.elementary.can.parser.fms;

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

	public void testPGN_F000() throws Exception {
		PGN_F000 pgn = new PGN_F000();
		long v = 0;
		v = (long) ((((15.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spn900.value = (1.0 * v + 0.0);
		v = (long) ((((125.0 - -125.0) * Math.random() + -125.0)- -125.0) / 1.0);
		pgn.spn520.value = (1.0 * v + -125.0);
		testPGN(pgn);
	}

	public void testPGN_F001() throws Exception {
		PGN_F001 pgn = new PGN_F001();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_F003() throws Exception {
		PGN_F003 pgn = new PGN_F003();
		long v = 0;
		v = (long) ((((100.0 - 0.0) * Math.random() + 0.0)- 0.0) / 0.4);
		pgn.spn91.value = (0.4 * v + 0.0);
		testPGN(pgn);
	}

	public void testPGN_F004() throws Exception {
		PGN_F004 pgn = new PGN_F004();
		long v = 0;
		v = (long) ((((125.0 - -125.0) * Math.random() + -125.0)- -125.0) / 1.0);
		pgn.spn513.value = (1.0 * v + -125.0);
		v = (long) ((((8031.875 - 0.0) * Math.random() + 0.0)- 0.0) / 0.125);
		pgn.spn190.value = (0.125 * v + 0.0);
		testPGN(pgn);
	}

	public void testPGN_F005() throws Exception {
		PGN_F005 pgn = new PGN_F005();
		long v = 0;
		v = (long) ((((125.0 - -125.0) * Math.random() + -125.0)- -125.0) / 1.0);
		pgn.spn524.value = (1.0 * v + -125.0);
		v = (long) ((((125.0 - -125.0) * Math.random() + -125.0)- -125.0) / 1.0);
		pgn.spn523.value = (1.0 * v + -125.0);
		testPGN(pgn);
	}

	public void testPGN_FDA5() throws Exception {
		PGN_FDA5 pgn = new PGN_FDA5();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FDD1() throws Exception {
		PGN_FDD1 pgn = new PGN_FDD1();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FE4E() throws Exception {
		PGN_FE4E pgn = new PGN_FE4E();
		long v = 0;
		v = (long) ((((15.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spn1821.value = (1.0 * v + 0.0);
		testPGN(pgn);
	}

	public void testPGN_FE58() throws Exception {
		PGN_FE58 pgn = new PGN_FE58();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FE6C() throws Exception {
		PGN_FE6C pgn = new PGN_FE6C();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FEAA() throws Exception {
		PGN_FEAA pgn = new PGN_FEAA();
		long v = 0;
		v = (long) ((((32127.5 - 0.0) * Math.random() + 0.0)- 0.0) / 0.5);
		pgn.spn582.value = (0.5 * v + 0.0);
		v = (long) ((((128510.0 - 0.0) * Math.random() + 0.0)- 0.0) / 2.0);
		pgn.spn180.value = (2.0 * v + 0.0);
		v = (long) ((((128510.0 - 0.0) * Math.random() + 0.0)- 0.0) / 2.0);
		pgn.spn181.value = (2.0 * v + 0.0);
		testPGN(pgn);
	}

	public void testPGN_FEAE() throws Exception {
		PGN_FEAE pgn = new PGN_FEAE();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FEBF() throws Exception {
		PGN_FEBF pgn = new PGN_FEBF();
		long v = 0;
		v = (long) ((((7.8125 - -7.8125) * Math.random() + -7.8125)- -7.8125) / 0.0625);
		pgn.spn907.value = (0.0625 * v + -7.8125);
		v = (long) ((((7.8125 - -7.8125) * Math.random() + -7.8125)- -7.8125) / 0.0625);
		pgn.spn908.value = (0.0625 * v + -7.8125);
		testPGN(pgn);
	}

	public void testPGN_FEC1() throws Exception {
		PGN_FEC1 pgn = new PGN_FEC1();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FED5() throws Exception {
		PGN_FED5 pgn = new PGN_FED5();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FEDF() throws Exception {
		PGN_FEDF pgn = new PGN_FEDF();
		long v = 0;
		v = (long) ((((125.0 - -125.0) * Math.random() + -125.0)- -125.0) / 1.0);
		pgn.spn514.value = (1.0 * v + -125.0);
		testPGN(pgn);
	}

	public void testPGN_FEE6() throws Exception {
		PGN_FEE6 pgn = new PGN_FEE6();
		long v = 0;
		v = (long) ((((62.5 - 0.0) * Math.random() + 0.0)- 0.0) / 0.25);
		pgn.spn959.value = (0.25 * v + 0.0);
		v = (long) ((((250.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spn960.value = (1.0 * v + 0.0);
		v = (long) ((((250.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spn961.value = (1.0 * v + 0.0);
		v = (long) ((((250.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spn963.value = (1.0 * v + 0.0);
		v = (long) ((((62.5 - 0.0) * Math.random() + 0.0)- 0.0) / 0.25);
		pgn.spn962.value = (0.25 * v + 0.0);
		v = (long) ((((2235.0 - 1985.0) * Math.random() + 1985.0)- 1985.0) / 1.0);
		pgn.spn964.value = (1.0 * v + 1985.0);
		testPGN(pgn);
	}

	public void testPGN_FEE8() throws Exception {
		PGN_FEE8 pgn = new PGN_FEE8();
		long v = 0;
		v = (long) ((((501.99 - 0.0) * Math.random() + 0.0)- 0.0) / 0.0078125);
		pgn.spn165.value = (0.0078125 * v + 0.0);
		v = (long) ((((250.996 - 0.0) * Math.random() + 0.0)- 0.0) / 0.00390625);
		pgn.spn517.value = (0.00390625 * v + 0.0);
		v = (long) ((((5531.875 - -2500.0) * Math.random() + -2500.0)- -2500.0) / 0.125);
		pgn.spn580.value = (0.125 * v + -2500.0);
		testPGN(pgn);
	}

	public void testPGN_FEE9() throws Exception {
		PGN_FEE9 pgn = new PGN_FEE9();
		long v = 0;
		v = (long) ((((2105540607.5 - 0.0) * Math.random() + 0.0)- 0.0) / 0.5);
		pgn.spn250.value = (0.5 * v + 0.0);
		testPGN(pgn);
	}

	public void testPGN_FEEE() throws Exception {
		PGN_FEEE pgn = new PGN_FEEE();
		long v = 0;
		v = (long) ((((210.0 - -40.0) * Math.random() + -40.0)- -40.0) / 1.0);
		pgn.spn174.value = (1.0 * v + -40.0);
		testPGN(pgn);
	}

	public void testPGN_FEEF() throws Exception {
		PGN_FEEF pgn = new PGN_FEEF();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FEF1() throws Exception {
		PGN_FEF1 pgn = new PGN_FEF1();
		long v = 0;
		v = (long) ((((250.996 - 0.0) * Math.random() + 0.0)- 0.0) / 0.00390625);
		pgn.spn84.value = (0.00390625 * v + 0.0);
		v = (long) ((((3.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spn597.value = (1.0 * v + 0.0);
		testPGN(pgn);
	}

	public void testPGN_FEF2() throws Exception {
		PGN_FEF2 pgn = new PGN_FEF2();
		long v = 0;
		v = (long) ((((3212.75 - 0.0) * Math.random() + 0.0)- 0.0) / 0.05);
		pgn.spn183.value = (0.05 * v + 0.0);
		v = (long) ((((127.0 - 0.0) * Math.random() + 0.0)- 0.0) / 0.001953125);
		pgn.spn184.value = (0.001953125 * v + 0.0);
		testPGN(pgn);
	}

	public void testPGN_FEF3() throws Exception {
		PGN_FEF3 pgn = new PGN_FEF3();
		long v = 0;
		v = (long) ((((211.1008122 - -210.0) * Math.random() + -210.0)- -210.0) / 0.0000001);
		pgn.spn584.value = (0.0000001 * v + -210.0);
		v = (long) ((((211.1008122 - -210.0) * Math.random() + -210.0)- -210.0) / 0.0000001);
		pgn.spn585.value = (0.0000001 * v + -210.0);
		testPGN(pgn);
	}

	public void testPGN_FEF5() throws Exception {
		PGN_FEF5 pgn = new PGN_FEF5();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FEF7() throws Exception {
		PGN_FEF7 pgn = new PGN_FEF7();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FEF8() throws Exception {
		PGN_FEF8 pgn = new PGN_FEF8();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FEFC() throws Exception {
		PGN_FEFC pgn = new PGN_FEFC();
		long v = 0;
		testPGN(pgn);
	}

	public void testPGN_FF55() throws Exception {
		PGN_FF55 pgn = new PGN_FF55();
		long v = 0;
		v = (long) ((((4211081000.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spn917b.value = (1.0 * v + 0.0);
		v = (long) ((((25.5 - 0.0) * Math.random() + 0.0)- 0.0) / 0.1);
		pgn.mfbp.value = (0.1 * v + 0.0);
		v = (long) ((((25.5 - 0.0) * Math.random() + 0.0)- 0.0) / 0.1);
		pgn.mrbp.value = (0.1 * v + 0.0);
		v = (long) ((((3.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spncep.value = (1.0 * v + 0.0);
		v = (long) ((((3.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spnceg.value = (1.0 * v + 0.0);
		v = (long) ((((3.0 - 0.0) * Math.random() + 0.0)- 0.0) / 1.0);
		pgn.spncec.value = (1.0 * v + 0.0);
		testPGN(pgn);
	}

	public void testPGN_FF5D() throws Exception {
		PGN_FF5D pgn = new PGN_FF5D();
		long v = 0;
		v = (long) ((((21055406000.0 - 0.0) * Math.random() + 0.0)- 0.0) / 5.0);
		pgn.spn917.value = (5.0 * v + 0.0);
		v = (long) ((((100.0 - 0.0) * Math.random() + 0.0)- 0.0) / 0.4);
		pgn.spn96b.value = (0.4 * v + 0.0);
		testPGN(pgn);
	}


	
}

