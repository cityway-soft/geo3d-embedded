package org.avm.device.generic.girouette.mobitec.protocol;

import java.text.MessageFormat;

import org.avm.device.girouette.GirouetteProtocol;

public class ICU400 extends GirouetteProtocol {

	/*
	 * pour test sur le calculateur : stty -F /dev/ttyS2 evenp cstopb -ixon raw
	 * speed 1200
	 * 
	 * 
	 * DEVICE_CATEGORY : org.avm.device.girouette.Girouette DEVICE_DESCRIPTION :
	 * Controleur de girouette mobitec DEVICE_MANUFACTURER : mobitec
	 * DEVICE_MODEL : org.avm.device.girouette.mobitec DEVICE_NAME :
	 * org.avm.device.girouette.mobitec DEVICE_SERIAL :
	 * 4df3687a-9b67-46c5-b83f-b581c98feff2 DEVICE_VERSION : 1.0.0 url :
	 * rs485:4;baudrate=1200;stopbits=2;parity=even;bitsperchar=7;blocking=off
	 * protocol : ICU400
	 */

	private static final byte STX = 0x7a;

	private static final byte ETX = 0x0d;

	private static final byte NULL = '0';

	private byte[] DEFAULT_VALUE = { STX, NULL, NULL, NULL, ETX, NULL };

	private byte[] _buffer = DEFAULT_VALUE;

	static {
		GirouetteProtocolFactory.factory.put(ICU400.class, new ICU400());
	}

	public ICU400() {

	}

	public byte[] generateDestination(final String code) {
		try {
			Integer value = new Integer(Integer.parseInt(code));
			Object[] tab = { value };
			byte[] buffer = MessageFormat.format("{0,number,000}", tab)
					.getBytes();

			for (int i = 0; i < 3; i++) {
				_buffer[i + 1] = buffer[i];
			}
			_buffer[5] = checksum(_buffer);
		} catch (Throwable e) {
			_buffer = DEFAULT_VALUE;
		}
		return _buffer;
	}

	private byte checksum(byte[] value) {

		byte chksum = 0;
		for (int i = 0; i < value.length; i++) {
			chksum = (byte) (chksum ^ value[i]);
			if (value[i] == ETX)
				break;
		}
		chksum = (byte) (chksum ^ 0x7F);
		return chksum;
	}

	// public byte[] generateStatus() {
	// String req = "61310D";
	// byte[] buf = fromHexaString(req);
	// byte chk = checksum(buf);
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// try {
	// out.write(buf);
	// out.write(chk);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return out.toByteArray();
	// }
	//
	// public int checkStatus(String status) {
	// System.out.println(this.getClass().getName() +
	// " checkStatus reponse ("+status+")");
	// return status.startsWith("61300D")?0:-1;
	// }
}
