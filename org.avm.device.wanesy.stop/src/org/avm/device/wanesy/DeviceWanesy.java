package org.avm.device.wanesy;

import org.avm.device.knet.model.KmsMarshaller;

public interface DeviceWanesy {

	public boolean isConnected();

	public boolean send(KmsMarshaller kms);

	public void removeDeviceListener(
			DeviceWanesyListener deviceWanesyInterfaceService);

	public void addDeviceListener(
			DeviceWanesyListener deviceWanesyInterfaceService);

}
