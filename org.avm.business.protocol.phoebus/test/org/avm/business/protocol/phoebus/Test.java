package org.avm.business.protocol.phoebus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class Test extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PhoebusTest.class);
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

		try {

			// bin -> obj
			ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
			Message bMessage = MessageFactory.parseBitstream(bin);
			System.out.println("\n bin -> obj " + bMessage);

			// obj -> xml
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bMessage.marshal(out);
			System.out
					.println("\n obj -> xml " + new String(out.toByteArray()));

			// xml -> obj
			ByteArrayInputStream xml = new ByteArrayInputStream(out
					.toByteArray());
			Message xMessage = MessageFactory.parseXmlstream(xml);
			System.out.println("\n xml -> obj " + xMessage);

			// obj -> bin
			out.reset();
			xMessage.put(out);
			return out.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}
	public void test() throws Exception {
		DemandeStatut message = new DemandeStatut();
		
		String arg0 = "0C010000000010001001000740014E1D0BA307804E1D0BA340";
		System.out.println("\n bin " + arg0);
		String arg1 = toHexaString(test(fromHexaString(arg0)));
		System.out.println("\n obj -> bin " + arg1);
		assertEquals(arg0, arg1);
	}
}