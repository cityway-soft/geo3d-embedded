package org.avm.device.generic.girouette.duhamel;

import junit.framework.TestCase;

import org.avm.device.generic.girouette.duhamel.protocol.GirouetteProtocolFactory;
import org.avm.device.girouette.GirouetteProtocol;

public class TestPCE3xx extends TestCase {
	
	public void debug(String string){
		System.out.println(string);
	}
	
	
	private String generate(String code){
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create("PCE3xx");
		return GirouetteProtocol.toHexaAscii(protocol2.generateDestination(code));
	}

	public void testCode3() {
		String code = "3";
		String trame = generate(code);
		String expected = "023A30303030303303";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode05() {
		String code = "05";
		String trame = generate(code);
		String expected = "023A30303030303503";
		debug("code=" + code + "=>" + trame);

		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode0007() {
		String code = "0007";
		String trame = generate(code);
		String expected = "023A30303030303703";
		debug("code=" + code + "=>" + trame);

		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode9997() {
		String code = "9997";
		String trame = generate(code);
		String expected = "023A30303939393703";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode9998() {
		String code = "9998";
		String trame = generate(code);
		String expected = "023A30303939393803";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode1001() {
		String code = "1001";
		String trame = generate(code);
		String expected = "023A30303130303103";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

}
