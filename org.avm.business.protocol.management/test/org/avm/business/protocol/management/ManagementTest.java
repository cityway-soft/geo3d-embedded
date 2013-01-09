package org.avm.business.protocol.management;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class ManagementTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ManagementTest.class);
	}

	public static String toHexaString(byte[] data) {
		byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	public static byte[] fromHexaString(String hexaString) {
		byte[] buffer = hexaString.getBytes();
		byte[] data = new byte[buffer.length / 2];
		for (int i = 0; i < data.length; i++) {
			int index = i * 2;
			int rValue = (buffer[i * 2] > 0x39) ? buffer[index] - 0x37
					: buffer[index] - 0x30;
			int lValue = (buffer[i * 2 + 1] > 0x39) ? buffer[index + 1] - 0x37
					: buffer[index + 1] - 0x30;
			data[i] = (byte) (((rValue << 4) & 0xF0) | (lValue & 0x0F));

		}
		return data;
	}

	private byte[] test(byte[] buffer) throws Exception {

		// bin -> obj
		ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
		Management bMessage = MessageFactory.get(bin);
		System.out.println("\n bin -> obj " + bMessage);

		// obj -> xml
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bMessage.marshal(out);
		System.out.println("\n obj -> xml " + new String(out.toByteArray()));

		// xml -> obj
		ByteArrayInputStream xml = new ByteArrayInputStream(out.toByteArray());
		Management xMessage = MessageFactory.unmarshal(xml);
		System.out.println("\n xml -> obj " + xMessage);

		// obj -> bin
		out.reset();
		xMessage.put(out);
		return out.toByteArray();
	}

	/*
	 * Test method for 'org.avm.business.protocol.phoebus.Alerte.Alerte()'
	 */
	public void testManagement() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Management message = new Management();
		message.setText("Message de test");

		message.put(out);
		out.close();

		String arg0 = toHexaString(out.toByteArray());
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(out.toByteArray()));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);

	}

}
