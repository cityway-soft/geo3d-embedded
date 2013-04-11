
package org.avm.device.generic.girouette.aesys;

import junit.framework.TestCase;

import org.avm.device.generic.girouette.aesys.protocol.GirouetteProtocolFactory;
import org.avm.device.girouette.GirouetteProtocol;

public class TestKC640_32G extends TestCase {
	
	public void debug(String string) {
	
		System.out.println(string);
	}
	
	private String generate(String code){
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create( "KC640_32G");
		return GirouetteProtocol.toHexaAscii(protocol2.generateDestination(code));
	}
	
	public void testCode123456() {
		String code = "123456";
		String trame = generate(code);
		System.out.println("Code(" + code + ")    => " + trame);
		String expected="02FFFE53444130303036313233343536035A5A5A5A"; //checksum ignoré
//		String expected="02FFFE534441303030363132333435360330344435";  // checksum calculé
		assertEquals(expected, trame);
	}
	
	public void testCode9875() {
		String code = "9875";
		String trame = generate(code);
		System.out.println("Code(" + code + ")    => " + trame);
		String expected="02FFFE53444130303036303039383735035A5A5A5A"; //checksum ignoré
//		String expected="02FFFE534441303030363030393837350330344444";// checksum calculé
		assertEquals(expected, trame);
	}
	
	
	public void testCode6() {
		String code = "9";
		String trame = generate(code);
		System.out.println("Code(" + code + ")       => " + trame);
		String expected="02FFFE53444130303036303030303039035A5A5A5A"; //checksum ignoré
//		String expected="02FFFE534441303030363030303030390330344339";// checksum calculé
		                 
		assertEquals(expected, trame);
	}

}
