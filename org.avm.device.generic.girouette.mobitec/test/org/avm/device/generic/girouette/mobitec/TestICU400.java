package org.avm.device.generic.girouette.mobitec;

import junit.framework.TestCase;

import org.avm.device.generic.girouette.mobitec.protocol.GirouetteProtocolFactory;
import org.avm.device.girouette.GirouetteProtocol;


public class TestICU400 extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	private String generate(String code){
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create( "ICU400");
		return GirouetteProtocol.toHexaAscii(protocol2.generateDestination(code));
	}
	
	public void testCode3() {
		String code = "3";
		String trame = generate(code);

		String expected = "7A3030330D3B";
		debug("code=" + code + "   =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode03() {
		String code = "03";
		String trame = generate(code);
		String expected = "7A3030330D3B";
		debug("code=" + code + "  =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode27() {
		String code = "27";
		String trame =generate(code);
		String expected = "7A3032370D3D";
		debug("code=" + code + "  =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode123() {
		String code = "123";
		String trame = generate(code);
		String expected = "7A3132330D38";
		debug("code=" + code + " =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode999() {
		String code = "999";
		String trame = generate(code);
		String expected = "7A3939390D31";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}
	
	public void testCode9999() {
		String code = "9999";
		String trame = generate(code);
		String expected = "7A3939390D31";
		debug("code=" + code + "=>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

}
