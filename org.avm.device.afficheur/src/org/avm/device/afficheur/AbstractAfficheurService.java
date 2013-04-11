package org.avm.device.afficheur;

import java.io.IOException;

import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public abstract class AbstractAfficheurService extends AbstractDriver implements
		Afficheur {

	private transient boolean started;

	private Port port;

	private AfficheurProtocol protocol;

	public AbstractAfficheurService(final ComponentContext context,
			final ServiceReference device) {

		super(context, device);
	}

	public void print(final String message) {

		if (!this.started) {
			this._log.error("[" + this + "] " + "driver not started");
			return;
		}
		this._log.info("Call sendCodeAfficheur " + "[" + this + "] " + message);

		try {
			sendMessage(message);
		} catch (final IOException e) {
			// retry one
			try {
				sendMessage(message);
			} catch (final IOException e1) {
				this._log.error(e.getMessage(), e);
			}
		}
	}

	public abstract AfficheurProtocol getAfficheurProtocol(String protocolName);

	protected void start(final DeviceConfig config) {
		final String url = config.getParamerter("url");
		this.port = new Port(url);
		final String protocolName = config.getParamerter("protocol");
		this.protocol = getAfficheurProtocol(protocolName);

		_log.info("protocol class="
				+ ((protocol == null) ? "null!" : protocol.getClass().getName()));
		this.started = true;
	}

	protected void stop() {

		this.started = false;
		this.port.close();
	}

	private void sendMessage(String message) throws IOException {

		try {
			if (protocol != null) {
				port.open();
				protocol.setInputStream(port.getInputStream());
				protocol.setOutputStream(port.getOutputStream());
				protocol.sendMessage(message);
				_log.debug("Destination Frame sent.");
			}
		} catch (final IOException e) {
			this.port.close();
			throw e;
		}

	}

	public int getStatus() throws Exception {
		_log.info("get status with protocol:" + protocol);
		int result = AfficheurProtocol.STATUS_ERR_NO_RESPONSE;

		try {
			if (protocol != null) {
				if (protocol.isStatusAvailable()) {
					port.open();
					protocol.setInputStream(port.getInputStream());
					protocol.setOutputStream(port.getOutputStream());
					result = protocol.getStatus();
					_log.debug("Request Status Frame sent.");
				} else {
					_log.debug("Status not available/implemented for protocol "
							+ protocol.getClass().getName());
					return AfficheurProtocol.STATUS_NOT_AVAILABLE;
				}
			}
		} catch (final IOException e) {
			this.port.close();
			throw e;
		}

		return result;
	}

}
