package org.angolight.hmi.swt.leds;

public interface LedsDevice {

	public void open();

	public boolean isOpen();

	public void close();

	public int setState(short states, byte period);

	public void setVisible(boolean visible);

	public void setBrightness(byte brightness);

	public int executeSequence(byte address, byte cycle, byte period);

	public void stopSequence();

	public void haltSequence();

	public int addState(byte address, short states, byte period);

	public int addStop(byte address, short states, byte period);
}
