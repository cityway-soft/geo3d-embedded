package org.avm.device.io;

public interface AnalogIODriver {

	public int getCapability();

	public int getBitResolution();

	public long getValue(int index);

	public void setValue(int index, long value);

}
