package org.avm.device.wanesy;

import org.avm.device.knet.model.KmsMarshaller;

public interface DeviceWanesyListener {

	public String getListeningOn();

	public void onDataReceived(KmsMarshaller kmsMarshaller);
}
