package org.avm.device.generic.io.k8055;

import org.osgi.framework.BundleContext;

public class K8055AnalogOutputService extends AbstractK8055AnalogIOService {

	public K8055AnalogOutputService(BundleContext context, K8055UsbDevice k8055) {
		super(context, k8055);
	}

	public int getBitResolution() {
		return 8;
	}

	public long getValue(int index) {
		throw new UnsupportedOperationException();
	}

	public void setValue(int index, long value) {
		_k8055.writeAnalogChannel(index, (byte) value);
	}

	protected String[] getSerial() {
		final String[] SERIAL = { "4e0c1812-22f5-41f4-b9d0-3c37a0ff6abd",
				"414d5838-e3a6-43ff-96c4-03bed560d889",
				"f2a1069b-a70f-4dff-a9cf-68bfdc4b3332",
				"c37b8845-beee-4bf0-9666-57dc1b0bfd09" };
		return SERIAL;
	}

	public int getCapability() {
		return 2;
	}
}
