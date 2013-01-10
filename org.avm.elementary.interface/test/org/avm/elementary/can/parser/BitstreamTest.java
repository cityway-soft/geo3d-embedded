package org.avm.elementary.can.parser;

import junit.framework.TestCase;

public class BitstreamTest extends TestCase {

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
	
	public void testGetbits() {
		
		Bitstream in = new Bitstream();
		byte[] b1 = { 0x00, (byte) 0x54, (byte) 0x55, 0x05 };
		System.out.println("[DSU] from : " + toHexaString(b1));
		in.initialize(b1);
		long value = in.getbits(10, 17);
		
		System.out.println("[DSU] value : "  +value);		
		
		Bitstream out = new Bitstream();
		byte[] b2 = new byte[4];
		out.initialize(b2);
		out.setbits(value, 10, 17);
		System.out.println("[DSU] to : " +toHexaString(b2));
		
		System.out.println();
		assertEquals(toHexaString(b1), toHexaString(b2));
	}

	public void testLittle_getbits() {
		
		Bitstream in = new Bitstream();
		byte[] b1 = { 0x00, (byte) 0x54, (byte) 0x55, 0x05 };
		System.out.println("[DSU] from : " + toHexaString(b1));
		in.initialize(b1);
		long value = in.little_getbits(10, 17);
		
		System.out.println("[DSU] value : "  +value);		
		
		Bitstream out = new Bitstream();
		byte[] b2 = new byte[4];
		out.initialize(b2);
		out.little_setbits(value, 10, 17);
		System.out.println("[DSU] to : " +toHexaString(b2));
		
		System.out.println();
		assertEquals(toHexaString(b1), toHexaString(b2));
		

	}

	public void testSetbits() {
		Bitstream in = new Bitstream();
		long v1 = 43693;
		System.out.println("[DSU] from : " +v1);
		byte[] buffer = new byte[4];
		in.initialize(buffer);
		in.setbits(v1, 10, 17);
		
		System.out.println("[DSU] buffer : " +toHexaString(buffer));
		
		Bitstream out = new Bitstream();
		in.initialize(buffer);
		long v2 = in.getbits(10, 17);
	
		System.out.println("[DSU] to : " +v2);

		System.out.println();
		assertEquals(v1,v2);
	}

	public void testLittle_setbits() {
		
		Bitstream in = new Bitstream();
		long v1 = 43693;
		System.out.println("[DSU] from : " +v1);
		byte[] buffer = new byte[4];
		in.initialize(buffer);
		in.little_setbits(v1, 10, 17);
		
		System.out.println("[DSU] buffer : " + toHexaString(buffer));
		
		Bitstream out = new Bitstream();
		in.initialize(buffer);
		long v2 = in.little_getbits(10, 17);
		System.out.println("[DSU] to : " +v2);
		
		System.out.println();
		assertEquals(v1,v2);
		// 43693 = 010-10101010-101101
		//101101001010101000000010
		//101101001010101000000010
	}

}
