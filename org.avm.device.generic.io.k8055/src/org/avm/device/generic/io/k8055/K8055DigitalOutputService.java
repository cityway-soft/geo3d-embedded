package org.avm.device.generic.io.k8055;

import org.osgi.framework.BundleContext;

public class K8055DigitalOutputService extends AbstractK8055DigitalIOService {

	public K8055DigitalOutputService(BundleContext context, K8055UsbDevice k8055) {
		super(context, k8055);
	}

	public boolean getValue(int index) {
		throw new UnsupportedOperationException();
	}

	public void setValue(int index, boolean value) {
		_k8055.writeDigitalChannel(index, value);
	}

	protected String[] getSerial() {
		final String[] SERIAL = { "6adc4454-0962-4bf1-a606-89a8c6d0c3da",
				"aa21e282-6cc4-4fda-b604-7724e12a366b",
				"c53b5f40-6928-4d03-b362-02460fb85916",
				"97bbe2c3-67d1-4cc5-a688-2c9b70d0269f" };
		return SERIAL;
	}

	public int getCapability() {
		return 8;
	}
}
