package org.avm.device.generic.girouette.aesys.protocol;

import java.text.MessageFormat;

import org.avm.device.girouette.GirouetteProtocol;

public class KC2S_2 extends GirouetteProtocol {

	private static final byte STX = 0x02;

	private static final byte ETX = 0x03;

	private static final byte ADDR = 'A';

	private static final byte TYPE = 'E';

	private static final byte NULL = '0';

	public static byte[] DEFAULT_VALUE;

	private final int OFFSET = 10;

	private byte[] buffer = new byte[DEFAULT_VALUE.length];

	static {
		DEFAULT_VALUE = new byte[] { KC2S_2.STX, KC2S_2.ADDR, 'V',
				'I', 'S', '0', '0', '1', '1', KC2S_2.TYPE,
				KC2S_2.NULL, KC2S_2.NULL, KC2S_2.NULL,
				KC2S_2.NULL, KC2S_2.NULL, KC2S_2.NULL,
				KC2S_2.NULL, KC2S_2.NULL, KC2S_2.NULL,
				KC2S_2.NULL, KC2S_2.ETX, 'Z', 'Z', 'Z', 'Z' };
		GirouetteProtocolFactory.factory.put(KC2S_2.class,
				new KC2S_2());
	}

	public KC2S_2() {
		System.arraycopy(DEFAULT_VALUE, 0, buffer, 0, buffer.length);
	}

	public byte[] generateDestination(final String code) {

		final Integer value = new Integer(Integer.parseInt(code));
		final Integer[] tab = { value };
		final byte[] msgcode = MessageFormat.format("{0,number,0000000000}",
				tab).getBytes();
		for (int i = 0; i < msgcode.length; i++) {
			this.buffer[i + OFFSET] = msgcode[i];
		}
		final byte[] checksum = checksum(this.buffer); // { 'Z', 'Z', 'Z', 'Z'
														// };
		int offset = buffer.length - checksum.length;
		for (int i = 0; i < checksum.length; i++) {
			this.buffer[offset + i] = checksum[i];
		}
		
		String trame = toHexaAscii(buffer);

		return trame.getBytes();
	}

	public byte[] checksum(byte[] data) {
		int i = 0;
		int checksum = 0;
		boolean finished = false;
		while (!finished) {
			checksum += data[i];
			if (i >= data.length || data[i] == KC2S_2.ETX) {
				finished = true;
			}
			i++;
		}

		byte[] result = new byte[4];
		for (i = 0; i < result.length; i++) {
			result[i] = (byte) (checksum & 0xF);
			checksum = (checksum >> 4);
		}

		return result;
	}


}
