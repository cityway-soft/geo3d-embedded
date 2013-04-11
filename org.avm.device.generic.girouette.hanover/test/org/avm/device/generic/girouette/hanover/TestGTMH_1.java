package org.avm.device.generic.girouette.hanover;

import junit.framework.TestCase;

import org.avm.device.generic.girouette.hanover.protocol.GirouetteProtocolFactory;
import org.avm.device.girouette.GirouetteProtocol;


public class TestGTMH_1 extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	private String generate(String code){
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create( "GTMH_1");
		return GirouetteProtocol.toHexaAscii(protocol2.generateDestination(code));
	}

	private String generateStatus(){
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create( "GTMH_1");
		return GirouetteProtocol.toHexaAscii(protocol2.generateStatus());
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
		String trame =generate(code);
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
