package org.avm.device.generic.girouette.hanover.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.avm.device.girouette.GirouetteProtocol;

public class GTMH_1 extends GirouetteProtocol {

	public static final byte STX = 0x02;
	public static final byte ETX = 0x03;
	public static final byte[] ACK = { 0x02, 0x30, 0x34, 0x06, 0x30, 0x32, 0x03 };
	public static final byte[] NAK = { 0x02, 0x30, 0x34, 0x15, 0x31, 0x31, 0x03 };
	public static final byte[] YES = { 0x02, 0x30, 0x34, 0x4F, 0x34, 0x3B, 0x03 };
	public static final byte[] NO = { 0x02, 0x30, 0x34, 0x4E, 0x34, 0x3A, 0x03 };
	public static final byte[] CHECK = { 0x02, 0x30, 0x35, 0x50, 0x43, 0x31,
			0x36, 0x03 };

	private Logger _log = Logger.getInstance(this.getClass());
	static {
		GirouetteProtocolFactory.factory.put(GTMH_1.class, new GTMH_1());
	}

	public GTMH_1() {

	}

	public byte[] generateDestination(final String code) {

		_log.info("destination : " + code + " " + this);
		Integer value = new Integer(Integer.parseInt(code));
		Object[] tab = { value };
		byte[] command = MessageFormat.format("z{0,number,0000}", tab)
				.getBytes();
		byte[] buffer = generate(command);

		return buffer;
	}

	private byte[] generate(byte[] command) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			buffer.write(STX);
			byte b = (byte) (command.length + 3);
			buffer.write(toHexaBytes(b));
			buffer.write(command);
			b = checksum(buffer.toByteArray(), 1, (command.length + 2));
			buffer.write(toHexaBytes(b));
			buffer.write(ETX);

		} catch (IOException e) {
			_log.error(e);
		}

		return buffer.toByteArray();
	}

	private byte checksum(byte[] data, int offset, int length) {
		byte result = 0;
		for (int i = offset; i < offset + length; i++) {
			result = (byte) (result ^ data[i]);
//			System.out.println("HanoverService.checksum() value : 0x"
//					+ Integer.toHexString(data[i]) + " XOR 0x"
//					+ Integer.toHexString(result));
		}
		return result;
	}

	protected byte[] toHexaBytes(byte data) {
		byte[] result = new byte[2];
		int rValue = data & 0x0F;
		int lValue = (data >> 4) & 0x0F;

		result[0] = (byte) (lValue + 0x30);
		result[1] = (byte) (rValue + 0x30);

		return result;

	}

	public byte[] generateStatus() {
		byte[] command = "a0".getBytes();
		byte[] buffer = generate(command);

		return buffer;
	}

	public int checkStatus(String status) {
		int result = -1;
		if (status!=null){
			result = status.startsWith("0230386130303030363903")?0:-2;
		}
		_log.debug("status value:" + status);
		return  result;
	}
}
