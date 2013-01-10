package org.avm.device.knet;

import org.avm.device.knet.model.KmsMarshaller;

public interface KmsHandler {
	public void handleMsg(KmsMarshaller msg);
}
