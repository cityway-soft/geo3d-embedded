package org.avm.device.generic.girouette.aesys.protocol;

import java.text.MessageFormat;

import org.avm.device.girouette.GirouetteProtocol;

public class KC640_32G extends GirouetteProtocol {

	private static final byte STX = 0x02;

	private static final byte ETX = 0x03;

	private static final byte IND_H = ((byte) 0xFF);

	private static final byte IND_L = ((byte) 0xFE);

	private static final byte LEN = 0x00;

	private static final byte CDE = 0x00;

	private static final byte CHK = 'Z';

	private static final byte TYPE = 'E';

	private static final byte NULL = '0';

	public static byte[] DEFAULT_VALUE;

	private final int OFFSET = 10;

	private byte[] buffer = new byte[DEFAULT_VALUE.length];

	static {
		DEFAULT_VALUE = new byte[] { STX, IND_H, IND_L, 'S', 'D', 'A', '0',
				'0', '0', '6', CDE, CDE, CDE, CDE, CDE, CDE, ETX, CHK, CHK,
				CHK, CHK };
		GirouetteProtocolFactory.factory.put(KC640_32G.class, new KC640_32G());
	}

	public KC640_32G() {
		System.arraycopy(DEFAULT_VALUE, 0, buffer, 0, buffer.length);
	}

	public byte[] generateDestination(final String code) {

		final Integer value = new Integer(Integer.parseInt(code));
		final Integer[] tab = { value };
		final byte[] msgcode = MessageFormat.format("{0,number,000000}", tab)
				.getBytes();
		for (int i = 0; i < msgcode.length; i++) {
			this.buffer[i + OFFSET] = msgcode[i];
		}

//		final byte[] checksum = checksum(this.buffer);
//		int offset = buffer.length - checksum.length;
//		for (int i = 0; i < checksum.length; i++) {
//			this.buffer[offset + i] = checksum[i];
//		}

		return buffer;// trame.getBytes();
	}

	public byte[] generateStatus() {
		String req = "02FFFE4D4D50303030300330334143";

		return fromHexaString(req);// trame.getBytes();
	}

	public byte[] checksum(byte[] data) {
		int i = 0;
		int checksum = 0;
		boolean finished = false;
		while (i < (data.length-4)) {
			checksum += data[i];
			i++;
		}

		checksum = checksum % 65536;

		byte[] result = new byte[4];
		for (i = 0; i < result.length; i++) {
			result[i] = (byte) (checksum & 0xF);
			checksum = (checksum >> 4);
		}

		return result;
	}

	public int checkStatus(String status) {
		int result = -1;
		if (status != null) {
			result = status.startsWith("028080524D4D30303239") ? 0 : -2;
		}
		return result;
	}

}
