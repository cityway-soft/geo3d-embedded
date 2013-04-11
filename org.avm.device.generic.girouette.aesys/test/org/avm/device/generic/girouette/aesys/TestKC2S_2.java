
package org.avm.device.generic.girouette.aesys;

import org.avm.device.generic.girouette.aesys.protocol.GirouetteProtocolFactory;
import org.avm.device.generic.girouette.aesys.protocol.KC2S_2;
import org.avm.device.girouette.GirouetteProtocol;

import junit.framework.TestCase;

public class TestKC2S_2 extends TestCase {
	
	public void debug(String string) {
	
		System.out.println(string);
	}
	
	private String generate(String code){
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create( "KC2S_2");
		return new String(protocol2.generateDestination(code));
	}
	
	public void testCode3() {
		String code = "3";
		String trame = generate(code);
		System.out.println("Code(" + code + ")       => " + trame);
		String expected = "02415649533030313145303030303030303030330302020400";
		assertEquals(expected, trame);
	}
	
	public void testCode9875() {
		String code = "9875";
		String trame =  generate(code);
		System.out.println("Code(" + code + ")    => " + trame);
		String expected="0241564953303031314530303030303039383735030C030400";
		assertEquals(expected, trame);
	}
	
	
	public void testCode6() {
		String code = "6";
		String trame =  generate(code);
		System.out.println("Code(" + code + ")       => " + trame);
		String expected="02415649533030313145303030303030303030360305020400";
		assertEquals(expected, trame);
	}
	
}
