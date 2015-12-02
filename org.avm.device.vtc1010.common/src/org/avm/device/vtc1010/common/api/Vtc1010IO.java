package org.avm.device.vtc1010.common.api;

public interface Vtc1010IO {
	
	public final static int BLOCKING_READ = 1;
	public final static int NON_BLOCKING_READ = 0;

	public int  open();

	public void close(int handle);

	public int read(int handle, int block);

	public void write(int handle, int value);
}
