package org.avm.device.io;

public interface CounterDriver {

	public int getCapability();

	public int getBitResolution();

	public int getValue(int index);

	public void reset(int index);

}
