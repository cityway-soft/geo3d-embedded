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
		return AfficheurProtocol.toHexaAscii(protocol2.generateStatus());
	}

	public void testCode3() {
		String code = "3";
		String trame = generate(code);

		String expected = "0230387A30303033373103";
		debug("code=" + code + "   =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode03() {
		String code = "03";
		String trame = generate(code);
		String expected = "0230387A30303033373103";
		debug("code=" + code + "  =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode27() {
		String code = "27";
		String trame = generate(code);
		String expected = "0230387A30303237373703";
		debug("code=" + code + "  =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode123() {
		String code = "123";
		String trame = generate(code);
		String expected = "0230387A30313233373203";
		debug("code=" + code + " =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode999() {
		String code = "999";
		String trame = generate(code);
		String expected = "0230387A30393939373B03";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode9999() {
		String code = "9999";
		String trame = generate(code);
		String expected = "0230387A39393939373203";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testGenerateStatus() {
		String trame = generateStatus();
		String expected = "0230387A39393939373203";
		debug("status request=" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

}
