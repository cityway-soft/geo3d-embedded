package org.avm.device.generic.afficheur.aesys.protocol;

import org.apache.log4j.Logger;
import org.avm.device.afficheur.AfficheurProtocol;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur DEVICE_DESCRIPTION :
 * Controleur d'afficheur Aesys DEVICE_MANUFACTURER : aesys. DEVICE_MODEL :
 * org.avm.device.generic.afficheur.aesys DEVICE_NAME :
 * org.avm.device.generic.afficheur.aesys DEVICE_SERIAL : DEVICE_VERSION : 1.0.0
 * url : rs485:0;baudrate=9600;bitsperchar=8;stopbits=1;parity=none;autocts=off;
 * autorts=off
 * 
 */
public class AESYS extends AfficheurProtocol {

	private final static char ENQ = 0x05;
	// private final static char STX = 0x02;
	private final static char ETX = 0x04;
	private final static char ADD = 'D';
	private final static char CTRL_W = 0x17;
	private final static char LUM = 'X';

	// private boolean _started;
	private static String _speed = "5";
	private static String _brightness = "0";
	private static int _nbCharBeforeTrunc = 16;
	private static boolean _noChecksum = false;
	private static char _address = ADD;

	private Logger _log = Logger.getInstance(this.getClass());
	static {
		AfficheurProtocolFactory.factory.put(AESYS.class, new AESYS());
	}

	public AESYS() {

	}

	/**
	 * Checksum function. Aesys checksum is an addition of characters in the
	 * frame modulo 0xffff the checksum is returned on a two bytes value
	 * 
	 * @param data
	 *            : the frame to checksum
	 * @return
	 */
	private static int checksum(final byte[] data) {
		int result = 0;
		for (int i = 0; i < data.length; i++) {
			result = (result + data[i]) % 0xffff;
		}
		return result;
	}

	public byte[] generateMessage(final String message) {
		final StringBuffer buffer = new StringBuffer();

		buffer.append(ENQ);
		buffer.append(_address);
		// buffer.append(STX);
		buffer.append(CTRL_W);
		buffer.append(LUM);
		buffer.append(_brightness);
		buffer.append(CTRL_W);
		buffer.append((message.length() > _nbCharBeforeTrunc) ? _speed : "0");
		String msg = removeCharWithAccent(message);
		buffer.append(msg);
		buffer.append(ETX);
		// calcul du checksum
		final int cs = checksum(buffer.toString().substring(2).getBytes());
		final StringBuffer chck = new StringBuffer();
		chck.append(cs);
		while (chck.length() < 5) {
			chck.insert(0, "0");
		}
		if (_noChecksum) {
			buffer.append("ZZZZZ");
		} else {
			buffer.append(chck.toString());
		}
		//
		return buffer.toString().getBytes();
	}

}
