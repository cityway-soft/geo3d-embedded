package org.avm.device.generic.io.k8055;

import org.osgi.framework.BundleContext;

public class K8055DigitalInputService extends AbstractK8055DigitalIOService {

	public K8055DigitalInputService(BundleContext context, K8055UsbDevice k8055) {
		super(context, k8055);
	}

	public boolean getValue(int index) {
		_log.info("[DSU] " + index + " " + _k8055.readDigitalChannel(index));
		return _k8055.readDigitalChannel(index);
	}

	public void setValue(int index, boolean value) {
		throw new UnsupportedOperationException();
	}

	protected String[] getSerial() {
		final String[] SERIAL = { "c18f84a9-997d-4edb-b7dd-90257e7fb775",
				"2270422d-2415-4540-a029-7820729e0ae8",
				"d4c49fb6-3dda-4826-bb8c-d89310f2ebe7",
				"2d38ab06-2853-4aed-9ece-650e99f436d4" };
		return SERIAL;
	}

	public int getCapability() {
		return 5;
	}

}
