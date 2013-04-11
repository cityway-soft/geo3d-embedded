package org.avm.device.generic.girouette.matis;

import junit.framework.TestCase;

import org.avm.device.generic.girouette.matis.protocol.GirouetteProtocolFactory;
import org.avm.device.girouette.GirouetteProtocol;

public class TestMATIS extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	private String generate(String code) {
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create("MATIS");
		return new String(protocol2
				.generateDestination(code));
	}

	private String generateStatus() {
		GirouetteProtocol protocol2 = GirouetteProtocolFactory.create("MATIS");
		return GirouetteProtocol.toHexaAscii(protocol2.generateStatus());
	}

	public void testChecksum1() {
		debug("---");
		String trame = ":0120202033\r\n";
		String subtrame = trame.substring(1, 11);
		debug("sub trame (" + subtrame.length() + ") for checksum " + subtrame);
		String cs = ProtocolHelper.checksum(subtrame.getBytes());
		debug("checksum :" + cs);
		assertEquals("6C", cs);
	}

	public void testChecksum2() {
		debug("---");
		String trame = ":100000000000100A20202020000000000000000056\r\n";
		String subtrame = trame.substring(1, trame.length() - 4);
		debug("sub trame (" + subtrame.length() + ") for checksum " + subtrame);
		String cs = ProtocolHelper.checksum(subtrame.getBytes());
		debug("checksum :" + cs);
		assertEquals("56", cs);

	}

	public void testChecksum3() {
		debug("---");
		String trame = ":100000000000030A2020203400000000000000004F\r\n";
		String subtrame = trame.substring(1, trame.length() - 4);
		debug("sub trame (" + subtrame.length() + ") for checksum " + subtrame);
		String cs = ProtocolHelper.checksum(subtrame.getBytes());
		debug("checksum :" + cs);
		assertEquals("4F", cs);
	}

	public void testChecksum4() {
		debug("---");
		String trame = ":100000000000020A30303035000000000000000052\r\n";
		String subtrame = trame.substring(1, trame.length() - 4);
		debug("sub trame (" + subtrame.length() + ") for checksum " + subtrame);
		String cs = ProtocolHelper.checksum(subtrame.getBytes());
		debug("checksum :" + cs);
		assertEquals("1F", cs);
	}

	public void testChecksum5() {
		debug("---");
		String trame = ":100000000000110A20202020000000000000000055\r\n";
		String subtrame = trame.substring(1, trame.length() - 4);
		debug("sub trame (" + subtrame.length() + ") for checksum " + subtrame);
		String cs = ProtocolHelper.checksum(subtrame.getBytes());
		debug("checksum :" + cs);
		assertEquals("55", cs);
	}

	public void testChecksum6() {
		debug("---");
		String trame = ":100000000000020A20202033000000000000000051\r\n";
		String subtrame = trame.substring(1, trame.length() - 4);
		debug("sub trame (" + subtrame.length() + ") for checksum " + subtrame);
		String cs = ProtocolHelper.checksum(subtrame.getBytes());
		debug("checksum :" + cs);
		assertEquals("51", cs);
	}

	public void testCode3() {
		String code = "3";
		String trame = generate(code);
		String expected = ":100000000000030A20202033000000000000000050\r\n";
		debug("code=" + code + "=>" + trame);
		assertEquals(expected.length(), 45);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode05() {
		String code = "05";
		String trame = generate(code);
		String expected = ":100000000000030A2020303500000000000000003E\r\n";
		debug("code=" + code + "=>" + trame);

		assertEquals(expected.length(), 45);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode0007() {
		String code = "0007";
		String trame = generate(code);
		String expected = ":100000000000030A3030303700000000000000001C\r\n";
		debug("code=" + code + "=>" + trame);

		assertEquals(expected.length(), 45);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode9997() {
		String code = "9997";
		String trame = generate(code);
		String expected = ":100000000000030A39393937000000000000000001\r\n";
		debug("code=" + code + "=>" + trame);
		assertEquals(expected.length(), 45);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode9998() {
		String code = "9998";
		String trame = generate(code);
		String expected = ":100000000000030A39393938000000000000000000\r\n";
		debug("code=" + code + "=>" + trame);
		assertEquals(expected.length(), 45);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

	public void testCode1001() {
		String code = "1001";
		String trame = generate(code);
		String expected = ":100000000000030A31303031000000000000000021\r\n";
		debug("code=" + code + "=>" + trame);
		assertEquals(expected.length(), 45);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

}
