package org.avm.device.generic.afficheur.mobitec.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.afficheur.Utils;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur DEVICE_DESCRIPTION :
 * Controleur d'afficheur Mobitec DEVICE_MANUFACTURER : mobitec DEVICE_MODEL :
 * org.avm.device.afficheur.mobitec DEVICE_NAME :
 * org.avm.device.afficheur.mobitec DEVICE_SERIAL :
 * 4df3687a-9b67-46c5-b83f-b581c98feff2 DEVICE_VERSION : 1.0.0 url :
 * rs485:2;baudrate
 * =1200;stopbits=2;parity=even;bitsperchar=7;autocts=off;autorts=off protocol :
 * NSI
 * 
 */
public class MOBITEC extends AfficheurProtocol {

	public static byte COMMAND = 'v';
	public static byte CR = 0x0D;

	private Logger _log = Logger.getInstance(this.getClass());
	static {
		AfficheurProtocolFactory.factory.put(MOBITEC.class, new MOBITEC());
	}

	public MOBITEC() {
	}

//	public void print(String message) {
//
//		_log.debug("print " + "[" + this + "] " + message);
//		byte[] buffer = generateMessage(Utils.format(message));
//		_log.debug("end " + "[" + this + "] " + toHexaString(buffer));
//
//		try {
//			send(buffer);
//		} catch (IOException e) {
//			// retry one
//			try {
//				send(buffer);
//			} catch (IOException e1) {
//				_log.error(e.getMessage(), e);
//			}
//		}
//	}

	public byte[] generateMessage(String message) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {
			String msg = Utils.format(message);
			buffer.write(COMMAND);
			buffer.write(msg.getBytes());
			buffer.write(CR);
			byte crc = (byte) (checksum(buffer.toByteArray(), 0, buffer.size()) ^ 0x7F);
			buffer.write(crc);
		} catch (IOException e) {
			_log.error(e);
		}

		return buffer.toByteArray();
	}

	private byte checksum(byte[] data, int offset, int length) {
		byte result = 0;
		for (int i = offset; i < offset + length; i++) {
			result = (byte) (result ^ data[i]);
			// System.out.println("[DSU] checksum value : 0x"
			// + Integer.toHexString(data[i]) + " XOR 0x"
			// + Integer.toHexString(result));
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
