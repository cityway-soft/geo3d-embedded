package org.angolight.device.leds;

public interface Leds {

	public static final int SEQUENCE_STOPPED = 1;
	
	public static final int OK = 0;
	public static final int BUSY = -1;
	public static final int ERROR = -2;

	public int M(short states, byte period, boolean check);

	public int L(byte brightgness, boolean check);

	public int I(byte address, short states, byte period, boolean check);

	public int J(byte address, short states, byte period, boolean check);

	public int X(byte address, byte cycle, byte period, boolean check);

	public int S(boolean check);

	public int T(boolean check);

	public int V(boolean check);
	
	public int R(byte address, boolean check);

}
