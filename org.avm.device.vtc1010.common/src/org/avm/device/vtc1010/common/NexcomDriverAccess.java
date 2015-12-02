package org.avm.device.vtc1010.common;

public class NexcomDriverAccess {


	static {
		System.loadLibrary("iodriver");
	}

	public static native int open();

	public static native void close(int handle);

	public static native int readInput(int handle, int block);

	public static native void setOutput(int handle, int value);

	public static native int readConfig(int handle);

	public static native void writeConfig(int handle, int value);
	
	public static native int readIgn(int handle);

}
