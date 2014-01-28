package org.avm.device.generic.afficheur.aesys;

import junit.framework.TestCase;

import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.generic.afficheur.aesys.protocol.AfficheurProtocolFactory;

public class TestAESYS extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	private String generate(String message) {
		AfficheurProtocol protocol2 = AfficheurProtocolFactory.create("AESYS");
		return AfficheurProtocol
				.toHexaAscii(protocol2.generateMessage(message));
	}

	private String generateStatus() {
		AfficheurProtocol protocol2 = AfficheurProtocolFactory.create("AESYS");
		return AfficheurProtocol.toHexaAscii(protocol2.generateStatus());
	}

	public void testCode3() {
		String code = "3";
		String trame = generate(code);

		// TODO
		String expected = "0544175830173033043030323835";
		debug("code=" + code + "   =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	
	public void testCodeMessage() {
		String code = "Mon test";
		String trame = generate(code);

		// TODO
		String expected = "054417583017304D6F6E2074657374043031303132";
		debug("code=" + code + "   =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}
}
