package org.avm.device.wanesy;

import org.avm.device.knet.model.KmsMarshaller;

public interface KnetConnectionListener {
	public void onEvent (KmsMarshaller kms);
}
