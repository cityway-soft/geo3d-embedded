package org.avm.device.generic.girouette.hanover;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.avm.device.girouette.Girouette;
import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class GirouetteService extends AbstractDriver implements Girouette {

	public static final byte STX = 0x02;
	public static final byte ETX = 0x03;
	public static final byte[] ACK = { 0x02, 0x30, 0x34, 0x06, 0x30, 0x32, 0x03 };
	public static final byte[] NAK = { 0x02, 0x30, 0x34, 0x15, 0x31, 0x31, 0x03 };
	public static final byte[] YES = { 0x02, 0x30, 0x34, 0x4F, 0x34, 0x3B, 0x03 };
	public static final byte[] NO = { 0x02, 0x30, 0x34, 0x4E, 0x34, 0x3A, 0x03 };
	public static final byte[] CHECK = { 0x02, 0x30, 0x35, 0x50, 0x43, 0x31,
			0x36, 0x03 };

	private Logger _log = Logger.getInstance(this.getClass());
	private Port _port;
	private String _protocol;
	private transient boolean _started;

	public GirouetteService(ComponentContext context, ServiceReference device) {
		super(context, device);
	}

	protected void start(DeviceConfig config) {
		String url = config.getParamerter("url");
		_protocol = config.getParamerter("protocol");
		_port = new Port(url);
		_started = true;
	}

	protected void stop() {
		_started = false;
		_port.close();
	}

	private boolean compare(byte[] array1, byte[] array2) {
		boolean result = true;
		if (array1 == null || array2 == null || array1.length != array2.length) {
			return false;
		}

		for (int i = 0; i < array2.length; i++) {
			if (array1[i] != array2[i]) {
				result = false;
				break;
			}
		}
		return result;
	}

	public void destination(String code) {

		if (!_started) {
			_log.error("[DSU] " + "[" + this + "] " + "driver not started");
			return;
		}

		_log.info("destination : " + code + " " + this);
		Integer value = new Integer(Integer.parseInt(code));
		Object[] tab = { value };
		byte[] command = MessageFormat.format("z{0,number,0000}", tab)
				.getBytes();
		byte[] buffer = generate(command);

		_log.info("[DSU] send : " + toHexaString(buffer));
		try {
			send(buffer);
		} catch (IOException e) {
			// retry one
			try {
				send(buffer);
			} catch (Exception e1) {
				_log.error(e);
			}
		}
	}

	private void send(byte[] buffer) throws IOException {

		try {
			OutputStream out = _port.getOutputStream();
			out.write(buffer);
		} catch (IOException e) {
			_port.close();
			throw e;
		}

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
			System.out.println("HanoverService.checksum() value : 0x"
					+ Integer.toHexString(data[i]) + " XOR 0x"
					+ Integer.toHexString(result));
		}
		return result;
	}

	private byte[] toHexaBytes(byte data) {
		byte[] result = new byte[2];
		int rValue = data & 0x0F;
		int lValue = (data >> 4) & 0x0F;

		// SOCRIE-B GTMH-1
		if (_protocol.equalsIgnoreCase("SOCRIE-B")) {
			result[0] = (byte) ((lValue > 9) ? lValue + 0x37 : lValue + 0x30);
			result[1] = (byte) ((rValue > 9) ? rValue + 0x37 : rValue + 0x30);
		}
		// socrie A
		else {
			result[0] = (byte) (lValue + 0x30);
			result[1] = (byte) (rValue + 0x30);
		}
		return result;

	}

	private String toHexaString(byte[] data) {
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
