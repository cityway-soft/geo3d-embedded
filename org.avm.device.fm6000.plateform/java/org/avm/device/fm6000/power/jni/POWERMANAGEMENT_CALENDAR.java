package org.avm.device.fm6000.power.jni;

/**
 * This class is a simple calendar used to manage the Comvs Power Management
 * library.
 * 
 * @author g.pohu
 * 
 */
public class POWERMANAGEMENT_CALENDAR {

	public int YEAR; // unused
	public int MONTH; // unused
	public int DAY; // day of the month to wakeup platform
	public int DAYOFWEEK; // day of the week to wakeup platform

	public int HOUR;
	public int MINUTE;
	public int SECOND;

	public POWERMANAGEMENT_CALENDAR() {
		YEAR = 0;
		MONTH = 0;
		DAY = 0;
		DAYOFWEEK = 0;
		HOUR = 0;
		MINUTE = 0;
		SECOND = 0;
	}

	public int getDAY() {
		return DAY;
	}

	public void setDAY(int day) {
		DAY = day;
	}

	public int getDAYOFWEEK() {
		return DAYOFWEEK;
	}

	public void setDAYOFWEEK(int dayofweek) {
		DAYOFWEEK = dayofweek;
	}

	public int getHOUR() {
		return HOUR;
	}

	public void setHOUR(int hour) {
		HOUR = hour;
	}

	public int getMINUTE() {
		return MINUTE;
	}

	public void setMINUTE(int minute) {
		MINUTE = minute;
	}

	public int getMONTH() {
		return MONTH;
	}

	public void setMONTH(int month) {
		MONTH = month;
	}

	public int getSECONDE() {
		return SECOND;
	}

	public void setSECOND(int seconde) {
		SECOND = seconde;
	}

	public int getYEAR() {
		return YEAR;
	}

	public void setYEAR(int year) {
		YEAR = year;
	}

	/**
	 * Set all date and time variables of Calendar.
	 * 
	 * @param year
	 *            not used for power management
	 * @param month
	 *            not used for power management
	 * @param day
	 *            wakeup day of the month : from 1 to 31
	 * @param dayofweek
	 *            wakeup day of the week : from 1 to 7
	 * @param hour
	 *            wakeup time : from 0 to 23
	 * @param minute
	 *            wakeup time : from 0 to 59
	 * @param second
	 *            wakeup time : from 0 to 59
	 */
	public void setFullDate(int year, int month, int day, int dayofweek,
			int hour, int minute, int second) {
		setYEAR(year);
		setMONTH(month);
		setDAY(day);
		setDAYOFWEEK(dayofweek);
		setHOUR(hour);
		setMINUTE(minute);
		setSECOND(second);
	}

	/**
	 * Set a wakeup date.
	 * 
	 * @param day
	 *            wakeup day of the month : from 1 to 31
	 * @param dayOfWeek
	 *            wakeup day of the week : from 1 to 7
	 * @param hour
	 *            wakeup time : from 0 to 23
	 * @param minute
	 *            wakeup time : from 0 to 59
	 * @param second
	 *            wakeup time : from 0 to 59
	 */
	public void setWakeUpDate(int day, int dayOfWeek, int hour, int minute,
			int second) {
		setDAY(day);
		setDAYOFWEEK(dayOfWeek);
		setHOUR(hour);
		setMINUTE(minute);
		setSECOND(second);
	}

	/**
	 * Create and return a String with all variables of the calendar class.
	 * 
	 * @return String : full string with all variables of the calendar class.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("TIME : " + HOUR + ":" + MINUTE
				+ ":" + SECOND + " - DATE : " + DAY + "/" + MONTH + "/" + YEAR
				+ " - DAY OF WEEK : " + DAYOFWEEK);
		return (new String(buffer));
	}

}
