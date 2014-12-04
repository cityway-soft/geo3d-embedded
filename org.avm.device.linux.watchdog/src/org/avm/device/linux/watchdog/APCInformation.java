package org.avm.device.linux.watchdog;

public interface APCInformation {
	public final static int STATE_APC = 1;
	public final static int STATE_PERMANENT = 2;
	public final static int STATE_ERROR = 0;
	
	public boolean open();
	public int read();
	public boolean close();
}
