package org.avm.device.generic.afficheur.hanover.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.afficheur.Utils;

public class GTMH_1 extends AfficheurProtocol {

	public static byte CLEAR = 'C';
	public static byte DISPLAY_TEXT = '0';
	public static byte SELF_TEST = '3';
	public static byte ADDRESS = '0';
	public static byte STX = 0x02;
	public static byte ETX = 0x03;

	private Logger _log = Logger.getInstance(this.getClass());
	static {
		AfficheurProtocolFactory.factory.put(GTMH_1.class, new GTMH_1());
	}

	public GTMH_1() {

	}

	public void clear() {

		_log.info("Clear");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(STX);
		buffer.write(CLEAR);
		buffer.write(ADDRESS);
		buffer.write(ETX);
		byte crc = checksum(buffer.toByteArray(), 1, buffer.size() - 1);
		int rValue = crc & 0x0F;
		int lValue = (crc >> 4) & 0x0F;
		buffer.write((byte) ((lValue > 9) ? lValue + 0x37 : lValue + 0x30));
		buffer.write((byte) ((rValue > 9) ? rValue + 0x37 : rValue + 0x30));

		_log.info("Send " + "[" + this + "] " + buffer);

		try {
			send(buffer.toByteArray());
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
		}
	}

	public void print(String message) {
		clear();
		_log.info("Print " + "[" + this + "] " + message);
		byte[] buffer = generateMessage(Utils.format(message));
		_log.info("Send " + "[" + this + "] " + new String(buffer));

		try {
			send(buffer);
		} catch (IOException e) {
			// retry one
			try {
				send(buffer);
			} catch (IOException e1) {
				_log.error(e.getMessage(), e);
			}
		}
	}

	public byte[] generateMessage(String message) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {
			buffer.write(STX);
			buffer.write(DISPLAY_TEXT);
			buffer.write(ADDRESS);
			buffer.write(message.getBytes());
			buffer.write(ETX);
			byte crc = checksum(buffer.toByteArray(), 1, buffer.size() - 1);
			int rValue = crc & 0x0F;
			int lValue = (crc >> 4) & 0x0F;
			buffer.write((byte) ((lValue > 9) ? lValue + 0x37 : lValue + 0x30));
			buffer.write((byte) ((rValue > 9) ? rValue + 0x37 : rValue + 0x30));
		} catch (IOException e) {
			_log.error(e);
		}

		return buffer.toByteArray();
	}

	private byte checksum(byte[] data, int offset, int length) {
		byte result = 0;
		for (int i = offset; i < offset + length; i++) {
			result += data[i];
		}
		return (byte) ((-result) % 256);
	}

//	public byte[] generateSelfTest() {
//		_log.info("Status");
//		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		buffer.write(STX);
//		buffer.write(SELF_TEST);
//		buffer.write(ADDRESS);
//		buffer.write(ETX);
//		byte crc = checksum(buffer.toByteArray(), 1, buffer.size() - 1);
//		int rValue = crc & 0x0F;
//		int lValue = (crc >> 4) & 0x0F;
//		buffer.write((byte) ((lValue > 9) ? lValue + 0x37 : lValue + 0x30));
//		buffer.write((byte) ((rValue > 9) ? rValue + 0x37 : rValue + 0x30));
//
//		_log.info("Send " + "[" + this + "] " + buffer);
//
//		try {
//			send(buffer.toByteArray());
//		} catch (IOException e) {
//			_log.error(e.getMessage(), e);
//		}
//		
//		
//		return buffer.toByteArray();
//	}


}
