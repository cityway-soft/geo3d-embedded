package org.avm.device.generic.girouette.spec;

import java.io.IOException;
import java.io.OutputStream;

import org.avm.device.girouette.Girouette;
import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class GirouetteService extends AbstractDriver implements Girouette {

	private transient boolean _started;

	private Port _port;

	/* exemple configuration 
		setmanufacturer spec.com
		setdescription Controleur de girouette SPEC
		setmodel org.avm.device.girouette.spec
		setname org.avm.device.girouette.spec
		setserial 4df3687a-9b67-46c5-b83f-b581c98feff2
		setcategory org.avm.device.girouette.Girouette
		setparameters url "comm:1;baudrate=1200;stopbits=1;parity=even;bitsperchar=8;blocking=off"
	*/

	public GirouetteService(final ComponentContext context,
			final ServiceReference device) {
		super(context, device);
	}

	private void send(final byte[] buffer) throws IOException {

		try {
			final OutputStream out = _port.getOutputStream();
			out.write(buffer);
		} catch (final IOException e) {
			_port.close();
			throw e;
		}

	}

	protected void start(final DeviceConfig config) {
		_log.debug("[FLA] in start");
		final String url = config.getParamerter("url");
		_port = new Port(url);
		_started = true;
		_log.debug("[FLA] end of start");
	}

	protected void stop() {
		_started = false;
		_port.close();
	}

	public void destination(final String code) {

		if (!_started) {
			_log.error("[DSU] driver not started");
			return;
		}

		_log.info("Call sendCodeGirouette :" + code);

		final byte[] buffer = ProtocolHelper.generate(code);
		final String debug = new String(buffer);
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buffer.length; ++i) {
			sb.append(buffer[i]);
			sb.append("(");
			sb.append((char) buffer[i]);
			sb.append(") ");
		}
		_log.info("Frame :" + sb.toString());
		_log.info("Frame : (" + debug.length() + ")" + debug.trim());
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

}
