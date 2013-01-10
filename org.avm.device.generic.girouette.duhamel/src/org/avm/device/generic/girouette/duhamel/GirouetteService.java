package org.avm.device.generic.girouette.duhamel;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.avm.device.girouette.Girouette;
import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class GirouetteService extends AbstractDriver implements Girouette {

	private transient boolean _started;

	private static final byte STX = 0x02;

	private static final byte ETX = 0x03;

	private static final byte SEP = ':';

	private static final byte NULL = '0';

	private byte[] DEFAULT_VALUE = { STX, SEP, NULL, NULL, NULL, NULL, NULL,
			NULL, ETX };

	private byte[] _buffer = DEFAULT_VALUE;

	private Port _port;

	public GirouetteService(ComponentContext context, ServiceReference device) {
		super(context, device);
	}

	protected void start(DeviceConfig config) {
		String url = config.getParamerter("url");
		String protocol = config.getParamerter("protocol");
		_port = new Port(url);
		_started = true;
	}

	protected void stop() {
		_started = false;
		_port.close();
	}

	public void destination(String code) {

		if (!_started) {
			_log.error("[DSU] " + "[" + this + "] " + "driver not started");
			return;
		}

		_log.info("Call sendCodeGirouette " + "[" + this + "] " + code);
		byte[] buffer = generate(code);
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
		} catch (IOException e) {
			_port.close();
			throw e;
		}

	}

	private byte[] generate(String code) {
		try {
			Integer value = new Integer(Integer.parseInt(code));
			Object[] tab = { value };
			byte[] buffer = MessageFormat.format("{0,number,000000}", tab)
					.getBytes();

			for (int i = 0; i < buffer.length; i++) {
				_buffer[i + 2] = buffer[i];
			}
		} catch (Throwable e) {
			_buffer = DEFAULT_VALUE;
		}
		return _buffer;
	}

}
