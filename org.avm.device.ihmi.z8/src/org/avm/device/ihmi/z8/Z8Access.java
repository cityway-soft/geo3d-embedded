package org.avm.device.ihmi.z8;

public class Z8Access {


	static {
		System.loadLibrary("z8.so");
	}
	
	public final static int SYSTEM_POWERED_DOWN = 0x01;
	public final static int SYSTEM_POWERED_DOWN_WITH_WAKEUP_TIMER = 0x02;
	public final static int SYSTEM_POWERED_UP_WITH_IGNITION_SIGNAL = 0x03;
	public final static int SYSTEM_STANDBY = 0x04;
	public final static int SYSTEM_POWERED_UP_AFTER_WAKEUP = 0x05;
	public final static int SYSTEM_WAITING_FOR_SHUTDOWN = 0x06;
	public final static int SYSTEM_WAITING_FOR_SHUTDOWN_TOO_HOT = 0x07;
	public final static int SYSTEM_WAITING_FOR_SYSTEM_TO_COOL_DOWN_POWER_DOWN = 0x08;
	public final static int SYSTEM_WAITING_FOR_SYSTEM_TO_COOL_STANDBY = 0x09;

	public static native int getSystemCurrentState();

	public static native int getBoardTemperature();

	public static float getPowerSuplyLevel() {
		return (float) (getVIN() / 5);
	}

	private static native int getVIN();

	public static native int getBacklightLevel();

	public static native void setBacklightLevel(int val);

	public static native int getLightLevel();

	public static native void setWatchdog(int val);

	public static native int getWatchdog();
	
	public static native int opendevice();
	
	public static native void closedevice(int deviceid);
	
	public static native int readSystemCurrentState(int deviceid);
	

}
