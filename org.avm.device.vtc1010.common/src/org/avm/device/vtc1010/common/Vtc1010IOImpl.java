package org.avm.device.vtc1010.common;

import org.avm.device.vtc1010.common.api.Vtc1010IO;

public class Vtc1010IOImpl implements Vtc1010IO {

	public int open() {
		return NexcomDriverAccess.open();
	}

	public void close(int handle) {
		NexcomDriverAccess.close(handle);
	}

	public int read(int handle, int block) {
		return NexcomDriverAccess.readInput(handle, block);
	}

	public void write(int handle, int value) {
		NexcomDriverAccess.setOutput(handle, value);
	}

}
