package org.avm.device.generic.afficheur.aesys;

import java.io.IOException;
import java.io.OutputStream;

import org.avm.device.afficheur.Afficheur;
import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur 
 * DEVICE_DESCRIPTION : Controleur d'afficheur Aesys
 * DEVICE_MANUFACTURER : aesys. 
 * DEVICE_MODEL : org.avm.device.generic.afficheur.aesys 
 * DEVICE_NAME : org.avm.device.generic.afficheur.aesys 
 * DEVICE_SERIAL : 
 * DEVICE_VERSION : 1.0.0
 * url : rs485:0;baudrate=9600;bitsperchar=8;stopbits=1;parity=none;autocts=off;autorts=off
 * 
 */
public class AfficheurService extends AbstractDriver implements Afficheur {

	private final static char ENQ = 0x05;
	private final static char STX = 0x02;
	private final static char ETX = 0x04;
	private final static char ADD = 'D';
	private final static char CTRL_W = 0x17;
	private final static char LUM = 'X';

	private Port _port;

	private boolean _started;
	private static String _speed;
	private static String _brightness;
	private static int _nbCharBeforeTrunc = 16;
	private static boolean _noChecksum = false;
	private static int _testMode = 0;
	private static char _address = ADD;

	public AfficheurService(final ComponentContext context,
			final ServiceReference device) {
		super(context, device);
	}

	/**
	 * Checksum function.
	 * Aesys checksum is an addition of characters in the frame modulo 0xffff
	 * the checksum is returned on a two bytes value
	 * @param data : the frame to checksum
	 * @return 
	 */
	private static int checksum(final byte[] data) {
		int result = 0;
		for (int i = 0; i < data.length; i++) {
			result = (result + data[i]) % 0xffff;
		}
		return result;
	}

	private static byte[] getDataLength(final String message) {
		final String tmp = Integer.toHexString(message.length());
		final StringBuffer ret = new StringBuffer();
		for (int i = 0; i < 4 - tmp.length(); ++i) {
			ret.append('0');
		}
		ret.append(tmp);
		return ret.toString().getBytes();
	}

	private static String toHexaString(final byte[] data) {
		final byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			final int rValue = data[i] & 0x0000000F;
			final int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	private String format(String message) {
		final StringBuffer buffer = new StringBuffer();
		message = message.toLowerCase();

		for (int i = 0; i < message.length(); i++) {
			final char c = message.charAt(i);

			switch (c) {
			case 'é':
			case 'è':
			case 'ê':
			case 'ë':
				buffer.append('e');
				break;
			case 'à':
			case 'â':
				buffer.append('a');
				break;
			case 'î':
			case 'ï':
				buffer.append('i');
				break;
			case 'ô':
				buffer.append('o');
				break;
			case 'ù':
			case 'û':
			case 'ü':
				buffer.append('u');
				break;
			case 'ç':
				buffer.append('c');
				break;
			default:
				buffer.append(c);
			}
		}

		return buffer.toString().toUpperCase();
	}

	private byte[] generate(final String message) {
		final StringBuffer buffer = new StringBuffer();

		buffer.append(ENQ);
		buffer.append(_address);
		//buffer.append(STX);
		buffer.append(CTRL_W);
		buffer.append(LUM);
		buffer.append(_brightness);
		buffer.append(CTRL_W);
		buffer.append((message.length() > _nbCharBeforeTrunc) ? _speed : "0");
		buffer.append(message);
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
			buffer.append(chck);
		}
		//
		return buffer.toString().getBytes();
	}

	private void send(final byte[] buffer) throws IOException {

		try {
			final OutputStream out = _port.getOutputStream();
			out.write(buffer);
			out.flush();
		} catch (final IOException e) {
			_port.close();
			throw e;
		}
	}

	protected void start(final DeviceConfig config) {
		final String url = config.getParamerter("url");
		_port = new Port(url);
		_speed = config.getParamerter("speed");
		if (_speed == null) {
			_speed = "5";
		}
		_brightness = config.getParamerter("brightness");
		if (_brightness == null) {
			_brightness = "0";
		}
		String tmp = config.getParamerter("truncate");
		if (tmp != null) {
			try {
				_nbCharBeforeTrunc = Integer.parseInt(tmp);
			} catch (final NumberFormatException nfe) {
				_log.error(nfe);
			}
		}
		tmp = config.getParamerter("test");
		if (tmp != null) {
			try {
				_testMode = Integer.parseInt(tmp);
				_log.info("_testMpde" + _testMode);
			} catch (final NumberFormatException nfe) {
				_log.error(nfe);
			}
		} else {
			_testMode = 0;
		}

		tmp = config.getParamerter("nochecksum");
		if (tmp != null) {
			if (tmp.equals("1")) {
				_noChecksum = true;
			} else {
				_noChecksum = false;
			}
		}
		tmp = config.getParamerter("address");
		if (tmp != null) {
			_address = (char) tmp.getBytes()[0];
		} else {
			_address = ADD;
		}
		_started = true;
	}

	protected void stop() {
		_started = false;
		_port.close();
	}

	private final static byte[] TEST_MODE1 = { 0x05, '@', 0x02, 0x17, 'X', '0',
			0x17, 'F', 'T', 'e', 's', 't', 0x18, 'F', ' ', 'a', 'f', 'f', 'i',
			'c', 'h', 'e', 'u', 'r', 0x04, 'Z', 'Z', 'Z', 'Z', 'Z' };
	private final static byte[] TEST_MODE2 = { 0x05, 'A', 0x02, 0x17, 'X', '0',
			0x17, 'F', 'T', 'e', 's', 't', 0x18, 'F', ' ', 'a', 'f', 'f', 'i',
			'c', 'h', 'e', 'u', 'r', 0x04, 'Z', 'Z', 'Z', 'Z', 'Z' };
	private final static byte[] TEST_MODE3 = { 0x05, 'D', 0x02, 0x17, 'X', '0',
			0x17, 'F', 'T', 'e', 's', 't', 0x18, 'F', ' ', 'a', 'f', 'f', 'i',
			'c', 'h', 'e', 'u', 'r', 0x04, 'Z', 'Z', 'Z', 'Z', 'Z' };

	public void print(final String message) {
		if (!_started) {
			_log.error("[" + this + "] " + "driver not started");
			return;
		}

		System.out.println("recap print : " + this);
		System.out.println("_address:" + _address);
		System.out.println("_noChecksum:" + _noChecksum);
		System.out.println("test:" + _testMode);
		System.out.println("speed:" + _speed);

		_log.info("print " + "[" + this + "] " + message);
		_log.info("test mode:" + _testMode);
		byte[] buffer;
		if (_testMode == 1) {
			buffer = TEST_MODE1;
		} else if (_testMode == 2) {
			buffer = TEST_MODE2;
		} else if (_testMode == 3) {
			buffer = TEST_MODE3;
		} else {
			buffer = generate(format(message));
		}
		_log.info("send " + "[" + this + "] " + toHexaString(buffer));

		try {
			send(buffer);
		} catch (final IOException e) {
			// retry one
			try {
				send(buffer);
			} catch (final IOException e1) {
				_log.error(e.getMessage(), e);
			}
		}
	}
	//	public static void main(final String[] args) {
	//		final StringBuffer data = new StringBuffer();
	//		data.append("PAGINA-");
	//		data.append((char) 0x17);
	//		data.append("E");
	//		data.append((char) 0x17);
	//		data.append("F1");
	//		data.append((char) 0x18);
	//		data.append("F");
	//		data.append((char) 0x18);
	//		data.append("E");
	//		data.append((char) 0x18);
	//		data.append("5PAGINA-");
	//		data.append((char) 0x17);
	//		data.append("E");
	//		data.append((char) 0x17);
	//		data.append("F2");
	//		data.append((char) 0x18);
	//		data.append("F");
	//		data.append((char) 0x18);
	//		data.append("E");
	//		data.append((char) 0x18);
	//		data.append("2PAGINA-");
	//		data.append((char) 0x17);
	//		data.append("E");
	//		data.append((char) 0x17);
	//		data.append("F3");
	//		data.append((char) 0x18);
	//		data.append("F");
	//		data.append((char) 0x18);
	//		data.append("E");
	//		data.append((char) 0x18);
	//		data.append("4");
	//		generate(data.toString());
	//	}

}
