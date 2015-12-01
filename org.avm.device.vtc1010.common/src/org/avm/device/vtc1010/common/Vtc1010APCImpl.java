package org.avm.device.vtc1010.common;

import org.avm.device.vtc1010.common.api.Vtc1010APC;

public class Vtc1010APCImpl implements Vtc1010APC {

	public int open() {
		return NexcomDriverAccess.open();
	}

	public int readState(int handle) {
		int state = NexcomDriverAccess.readIgn(handle);
		return (state == 0x0C) ? Vtc1010APC.APC_ON : Vtc1010APC.APC_OFF;
	}

	public void close(int handle) {
		NexcomDriverAccess.close(handle);
	}

}
