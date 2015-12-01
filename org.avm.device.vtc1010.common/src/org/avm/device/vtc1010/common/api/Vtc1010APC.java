package org.avm.device.vtc1010.common.api;

public interface Vtc1010APC {
	
	public final static int APC_ON = 1;
	public final static int APC_OFF = 0;
	
	public int open();

	public int readState(int handle);

	public void close(int handle);

}
