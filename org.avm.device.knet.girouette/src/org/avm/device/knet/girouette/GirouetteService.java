package org.avm.device.knet.girouette;

import org.avm.device.girouette.Girouette;
import org.avm.device.knet.KnetAgentFactory;
import org.avm.elementary.common.AbstractDriver;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class GirouetteService extends AbstractDriver implements Girouette {

	private transient boolean _started;

	private KnetDevice4Girouette _knetDevice;

	public GirouetteService(ComponentContext context, ServiceReference device) {
		super(context, device);
		_knetDevice = new KnetDevice4Girouette();
	}

	protected void start(DeviceConfig config) {
		_started = true;
	}

	protected void stop() {
		_started = false;
	}

	public void destination(String code) {
		if (!_started) {
			_log.error("[DSU] " + "[" + this + "] " + "driver not started");
			return;
		}
		_log.info("Call sendCodeGirouette " + "[" + this + "] " + code);
		_knetDevice.postCodeGirouette(code);
	}

	public void setAgent(KnetAgentFactory factory) {
		_knetDevice.setAgent(factory);
		_knetDevice.start();
	}

	public void unsetAgent() {
		_knetDevice.unsetAgent();
		_knetDevice.stop();
	}
}
