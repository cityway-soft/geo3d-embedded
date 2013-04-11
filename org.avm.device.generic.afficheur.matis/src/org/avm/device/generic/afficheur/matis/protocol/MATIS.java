package org.avm.device.generic.afficheur.matis.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.afficheur.Utils;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur DEVICE_DESCRIPTION :
 * Controleur d'afficheur Matis DEVICE_MANUFACTURER : matis DEVICE_MODEL :
 * org.avm.device.generic.afficheur.matis DEVICE_NAME :
 * org.avm.device.generic.afficheur.matis DEVICE_SERIAL :
 * 4df3687a-9b67-46c5-b83f-b581c98feff2 DEVICE_VERSION : 1.0.0 url :
 * rs485:2;baudrate
 * =1200;bitsperchar=8;stopbits=1;parity=even;autocts=off;autorts=off
 * 
 */
public class MATIS extends AfficheurProtocol {

	public static byte[] ENQ = { 0x02, 0x05 };
	public static byte[] CR = { 0x0D };
	public static byte[] ETX = { 0x02, 0x03 };
	public static byte[] ACK = { 0x02, 0x06 };
	public static byte[] EOT = { 0x02, 0x04 };
	public static byte INDICATOR_ADDRESS = (byte) '1'; // broadcast
	public static byte RECORD_TYPE = (byte) '1'; // broadcast
	public static byte FONT = 0x43; // font "C"

	private Logger _log = Logger.getInstance(this.getClass());
	static {
		AfficheurProtocolFactory.factory.put(MATIS.class, new MATIS());
	}

	public MATIS() {
	}

	public void print(String message) {

		_log.info("print " + "[" + this + "] " + message);
		byte[] buffer = generateMessage(Utils.format(message));
		_log.info("send " + "[" + this + "] " + toHexaString(buffer));

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
			buffer.write(ENQ);
			int length = (message.length() + 2) + 0x20;
			buffer.write((byte) length);
			buffer.write(INDICATOR_ADDRESS);
			buffer.write(RECORD_TYPE);
			buffer.write(CR);
			buffer.write(FONT);
			buffer.write(message.getBytes());
			buffer.write(CR);
			buffer.write(ETX);
			buffer.write(checksum(buffer.toByteArray()));
		} catch (IOException e) {
			_log.error(e);
		}

		return buffer.toByteArray();
	}

	private byte checksum(byte[] data) {
		byte result = data[0];
		for (int i = 1; i < data.length; i++) {
			result = (byte) (result ^ data[i]);
		}
		return result;
	}

	private static String toHexaString(byte[] data) {
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

}
