package org.avm.device.generic.io.k8055;

import javax.usb.util.UsbUtil;

import org.osgi.framework.BundleContext;

public class K8055AnalogInputService extends AbstractK8055AnalogIOService {

	public K8055AnalogInputService(BundleContext context, K8055UsbDevice k8055) {
		super(context, k8055);
	}

	public int getBitResolution() {
		return 8;
	}

	public long getValue(int index) {
		return UsbUtil.unsignedLong(_k8055.readAnalogChannel(index));
	}

	public void setValue(int index, int value) {
		throw new UnsupportedOperationException();
	}

	protected String[] getSerial() {
		final String[] SERIAL = { "75fd3be3-d3eb-407b-8327-8df46c29375f",
				"3bae4d67-7aa9-4efa-b95b-1bf1c67da3de",
				"c9aba136-cf5b-4495-a229-d7253c3fc1a1",
				"fc9f01a5-811e-4f9a-be76-509333cb2406" };
		return SERIAL;
	}

	public int getCapability() {
		return 2;
	}

	public void setValue(int index, long value) {
		// TODO Auto-generated method stub

	}

}
