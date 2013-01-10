package org.avm.device.fm6000.power.jni;

import java.util.HashMap;

public interface COMVS_POWERMANAGEMENTConstants {
	/**
	 * Unknown power source value
	 */
	public static final int COMVS_POWERSOURCE_UNKNOWN = 0x0;	// Unknown / error
	/**
	 * battery power source value
	 */
	public static final int COMVS_POWERSOURCE_BATTERY = 0x1;	// Internal or external battery
	/**
	 * Extern power source value
	 */
	public static final int COMVS_POWERSOURCE_APC_ACC = 0x2;	// APC or ACC source


	/**
	 * No wakeup set value
	 */
	public static final int COMVS_SYSWAKE_NONE = 0;				// No Wake UP / APC "Wake up"
	/**
	 * Digital IO pin 1 wakeup value
	 */
	public static final int COMVS_SYSWAKE_WAKEUP = 1;			// Digital IO Pin 1
	/**
	 * Accelerometer wakeup value
	 */
	public static final int COMVS_SYSWAKE_MOVEIN = 2;			// Accelerometer
	/**
	 * RTC wakeup value
	 */
	public static final int COMVS_SYSWAKE_RTC = 4;				// RTC wake up
	
	
	/**
	 * RTC wakeup config value : no wakeup
	 */
	public static final int COMVS_WUP_MATCH_INVALID = 0x00;		// No alarm armed - GetWakeupAlarm()
	/**
	 * RTC wakeup config value : Match Hours, Minutes and Seconds.
	 */
	public static final int COMVS_WUP_MATCH_HMS = 0x01;			// Match Hours, Minutes and Seconds.
	/**
	 * RTC wakeup config value : Match HMS + Date of the month.
	 */
	public static final int COMVS_WUP_MATCH_DATE_OF_MONTH = 0x02;// Match HMS + Date of the month.
	/**
	 * RTC wakeup config value : Match HMS + Day of the week.
	 */
	public static final int COMVS_WUP_MATCH_DAY_OF_WEEK = 0x03;	// Match HMS + Day of the week.
	
	
	/**
	 * HashMap of power source values and String description
	 */
	public final static HashMap powerSources = new HashMap();
	/**
	 * HashMap of wakeup source values and String description
	 */
	public final static HashMap wakeupSources = new HashMap();
	/**
	 * HashMap of wakeup configuration values and String description
	 */
	public final static HashMap wakeupConfig = new HashMap();

}
