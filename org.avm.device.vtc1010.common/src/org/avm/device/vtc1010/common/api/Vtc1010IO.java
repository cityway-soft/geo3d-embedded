package org.avm.device.vtc1010.common.api;

public interface Vtc1010IO {

	public int  open();

	public void close(int handle);

	public int read(int handle, int block);

	public void write(int handle, int value);
}
