package org.avm.device.generic.io.k8055;

import org.osgi.framework.BundleContext;

public class K8055CounterService extends AbstractK8055CounterService {

	public K8055CounterService(BundleContext context, K8055UsbDevice k8055) {
		super(context, k8055);
	}

	public int getBitResolution() {
		return 16;
	}

	public int getValue(int index) {
		return _k8055.readCounter(index);
	}

	public void reset(int index) {
		_k8055.resetCounter(index);
	}

	protected String[] getSerial() {
		final String[] SERIAL = { "25e6f74b-5719-4245-9671-1db36805115d",
				"45175eee-0003-400d-9367-66831b8f5612",
				"c87c44ec-5ac6-453b-b575-21c25c9c7106",
				"10c0fad9-c29c-4a1a-bd97-a7a68dfa4d9f" };
		return SERIAL;
	}

	public int getCapability() {
		return 2;
	}
}
