package org.avm.device.generic.afficheur.mobitec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.avm.device.afficheur.Afficheur;
import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur
 * DEVICE_DESCRIPTION : Controleur d'afficheur Mobitec
 * DEVICE_MANUFACTURER : mobitec
 * DEVICE_MODEL : org.avm.device.afficheur.mobitec
 * DEVICE_NAME : org.avm.device.afficheur.mobitec
 * DEVICE_SERIAL : 4df3687a-9b67-46c5-b83f-b581c98feff2
 * DEVICE_VERSION : 1.0.0
 * url : rs485:2;baudrate=1200;stopbits=2;parity=even;bitsperchar=7;autocts=off;autorts=off
 * protocol : NSI
 *
 */
public class AfficheurService extends AbstractDriver implements Afficheur {

	public static byte COMMAND = 'v';
	public static byte CR = 0x0D;

	private Port _port;

	private boolean _started;

	public AfficheurService(ComponentContext context, ServiceReference device) {
		super(context, device);
	}

	protected void start(DeviceConfig config) {
		String url = config.getParamerter("url");
		_port = new Port(url);
		_started = true;
	}

	protected void stop() {
		_started = false;
		_port.close();
	}

	public void print(String message) {
		if (!_started) {
			_log.error("[DSU] " + "[" + this + "] " + "driver not started");
			return;
		}

		_log.debug("[DSU] print " + "[" + this + "] " + message);
		byte[] buffer = generate(format(message));
		_log.debug("[DSU] send " + "[" + this + "] " + toHexaString(buffer));

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

	private void send(byte[] buffer) throws IOException {

		try {
			OutputStream out = _port.getOutputStream();
			out.write(buffer);
			out.flush();
		} catch (IOException e) {
			_port.close();
			throw e;
		}
	}

	public static final String format(String message) {
		StringBuffer buffer = new StringBuffer();
		message = message.toLowerCase();

		for (int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);

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
			case 'ï':
			case 'î':
				buffer.append('i');
				break;
			case 'ô':
				buffer.append('o');
				break;
			case 'û':
			case 'ù':
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

	private byte[] generate(String message) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {
			buffer.write(COMMAND);
			buffer.write(message.getBytes());
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
//			System.out.println("[DSU] checksum value : 0x"
//					+ Integer.toHexString(data[i]) + " XOR 0x"
//					+ Integer.toHexString(result));
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
