package org.avm.device.generic.afficheur.matis;

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
 * DEVICE_DESCRIPTION : Controleur d'afficheur Matis
 * DEVICE_MANUFACTURER : matis
 * DEVICE_MODEL : org.avm.device.generic.afficheur.matis
 * DEVICE_NAME : org.avm.device.generic.afficheur.matis
 * DEVICE_SERIAL : 4df3687a-9b67-46c5-b83f-b581c98feff2
 * DEVICE_VERSION : 1.0.0
 * url : rs485:2;baudrate=1200;bitsperchar=8;stopbits=1;parity=even;autocts=off;autorts=off
 * 
 */
public class AfficheurService extends AbstractDriver implements Afficheur {

	public static byte[] ENQ = { 0x02, 0x05 };
	public static byte[] CR = { 0x0D };
	public static byte[] ETX = { 0x02, 0x03 };
	public static byte[] ACK = { 0x02, 0x06 };
	public static byte[] EOT = { 0x02, 0x04 };
	public static byte INDICATOR_ADDRESS = (byte) '1'; // broadcast
	public static byte RECORD_TYPE = (byte) '1'; // broadcast
	public static byte FONT = 0x43; // font "C"

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

		_log.info("[DSU] print " + "[" + this + "] " + message);
		byte[] buffer = generate(format(message));
		_log.info("[DSU] send " + "[" + this + "] " + toHexaString(buffer));

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
