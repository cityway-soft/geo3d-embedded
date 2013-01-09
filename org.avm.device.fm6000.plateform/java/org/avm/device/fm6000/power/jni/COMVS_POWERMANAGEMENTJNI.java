package org.avm.device.fm6000.power.jni;

public class COMVS_POWERMANAGEMENTJNI {

	static {
		try {
			System.loadLibrary("comvs_power");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load. \n" + e);
		}
	}

	public static native int Comvs_SwitchOffAlt();

	public static native int Comvs_ColdReboot();

	public static native int Comvs_Modem_SwitchOn();

	public static native int Comvs_Modem_SwitchOff();

	public static native int Comvs_GPS_SwitchOn();

	public static native int Comvs_GPS_SwitchOff();

	public static native int Comvs_Accelerometer_SwitchOn();

	public static native int Comvs_Accelerometer_SwitchOff();

	public static native int Comvs_CAN_SwitchOn();

	public static native int Comvs_CAN_SwitchOff();

	public static native int Comvs_GetPowerSource();

	public static native int Comvs_StopPowerEvent();

	public static native int Comvs_GetPowerSourceAlt();

	public static native int Comvs_WaitPowerEvent(int timeout);

	public static native int Comvs_SetRTCWakeUp(
			POWERMANAGEMENT_CALENDAR wakeUpDate, int RTCWakeUpMode);

	public static native boolean Comvs_GetRTCWakeUp(
			POWERMANAGEMENT_CALENDAR wakeUpDate, int RTCWakeUpMode);

	public static native int Comvs_GetWakeupSource();

}
