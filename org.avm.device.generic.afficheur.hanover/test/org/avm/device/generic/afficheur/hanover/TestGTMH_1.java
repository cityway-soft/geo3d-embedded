package org.avm.device.generic.afficheur.hanover;

import junit.framework.TestCase;

import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.generic.afficheur.hanover.protocol.AfficheurProtocolFactory;

public class TestGTMH_1 extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	private String generate(String message) {
		AfficheurProtocol protocol2 = AfficheurProtocolFactory.create("GTMH_1");
		return AfficheurProtocol.toHexaAscii(protocol2.generateMessage(message));
	}

	private String generateStatus() {
		AfficheurProtocol protocol2 = AfficheurProtocolFactory.create("GTMH_1");
		byte[] status = protocol2.generateStatus();
		String hex = null;
		if (status != null){
			hex = AfficheurProtocol.toHexaAscii(status);
		}
		return hex;
	}

	public void testCode3() {
		String code = "3";
		String trame = generate(code);

		String expected = "02303033033641";
		debug("code=" + code + "   =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode03() {
		String code = "03";
		String trame = generate(code);
		String expected = "0230303033033341";
		debug("code=" + code + "  =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode27() {
		String code = "27";
		String trame = generate(code);
		String expected = "0230303237033334";
		debug("code=" + code + "  =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode123() {
		String code = "123";
		String trame = generate(code);
		String expected = "023030313233033037";
		debug("code=" + code + " =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode999() {
		String code = "999";
		String trame = generate(code);
		String expected = "023030393939034632";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode9999() {
		String code = "9999";
		String trame = generate(code);
		String expected = "02303039393939034239";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}


}
