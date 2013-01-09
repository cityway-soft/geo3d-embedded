package org.avm.device.io.iocardbus;

import org.avm.device.io.IOCardInfo;

public interface IOCardBus {

	public void registerDevice(IOCardInfo card);

	public void unregisterDevice(IOCardInfo card);

}
