package org.avm.device.generic.girouette.spec;

import junit.framework.TestCase;

import org.avm.device.generic.girouette.spec.protocol.GirouetteProtocolFactory;
import org.avm.device.girouette.GirouetteProtocol;

public class TestSPEC extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	private String generate(String code) {
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create("SPEC");
		return GirouetteProtocol.toHexaAscii(protocol2
				.generateDestination(code));
	}

	private String generateStatus() {
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create("SPEC");
		return GirouetteProtocol.toHexaAscii(protocol2.generateStatus());
	}

	public void testCode3() {
		String code = "3";
		String trame = generate(code);

		String expected = "02" + "3038" + "7A" + "30303033" + "373F" + "03";
		debug("code=" + code + "   =>" + trame);
		assertEquals(expected.length(), 22);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode03() {
		String code = "03";
		String trame = generate(code);
		String expected = "0230387A30303033373F03";
		debug("code=" + code + "  =>" + trame);
		assertEquals(expected.length(), 22);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode27() {
		String code = "27";
		String trame = generate(code);
		String expected = "02" + "3038" + "7A" + "30303237" + "3739" + "03";
		debug("code=" + code + "  =>" + trame);
		assertEquals(expected.length(), 22);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode123() {
		String code = "123";
		String trame = generate(code);
		String expected = "02" + "3038" + "7A" + "30313233" + "373C" + "03";
		debug("code=" + code + " =>" + trame);
		assertEquals(expected.length(), 22);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode9999() {
		String code = "9999";
		String trame = generate(code);
		String expected = "02" + "3038" + "7A" + "39393939" + "373C" + "03";
		debug("code=" + code + "=>" + trame);
		assertEquals(expected.length(), 22);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

}
