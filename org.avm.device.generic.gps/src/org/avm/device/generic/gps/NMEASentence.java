/*
 * Created on 14 sept. 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.avm.device.generic.gps;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

/**
 * @author Daniel SURU (MERCUR)
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NMEASentence {

	protected String _strSentence;
	protected String[] _sentence = new String[255];
	protected String _checksum;

	/**
	 * 
	 */
	public NMEASentence() {
		super();
	}

	public boolean isGGASentence() {
		if (_sentence == null)
			return false;
		String tmp=getString(0);
		return tmp.equals("$GPGGA") || tmp.equals("$GNGGA");
	}

	public boolean isRMCSentence() {
		if (_sentence == null)
			return false;
		String tmp=getString(0);
		return getString(0).equals("$GPRMC") || tmp.equals("$GNRMC");
	}

	private String[] split(String str, String tag) {
		Vector v = new Vector();
		boolean ok = true;
		String tmp = str;
		String token;
		int index = 0;
		while (ok) {
			index = tmp.indexOf(tag);
			if (index != -1) {
				token = tmp.substring(0, index);
				v.addElement(token);
				tmp = tmp.substring(index + tag.length());
			} else {
				v.addElement(tmp);
				ok = false;
			}
		}
		String[] result = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			result[i] = (String) v.elementAt(i);
		}
		return result;
	}

	public String initialize() {
		StringBuffer buffer = new StringBuffer();
		int field = 0;
		while (true) {
			if (_sentence[field] == null) {
				break;
			}
			if (field != 0) {
				buffer.append(',');
			}
			buffer.append(_sentence[field]);
			field++;
		}
		_strSentence = buffer.toString();
		setChecksum();
		return _strSentence + '*' + _checksum + '\r' + '\n';
	}

	public boolean parse(String sentence) {
		int index = sentence.indexOf('*');
		if (index == -1) {
			_strSentence = sentence;
			_checksum = null;
		} else {
			_strSentence = sentence.substring(0, index);
			_checksum = sentence.substring(index + 1, sentence.length());
		}
		boolean check = isChecksum();
		if (check == false) {
			return false;
		}
		String tmp = new String(_strSentence);
		_sentence = split(tmp, ",");
		return true;
	}

	public int getInteger(int field) {
		int result = 0;
		if (_sentence[field] != null && _sentence[field].length() > 0) {
			result = Integer.valueOf(_sentence[field]).intValue();
		}
		return result;
	}

	public void setInteger(int field, int value) {
		_sentence[field] = new String("" + value);
	}

	public boolean getBoolean(int field) {
		boolean result = false;
		if (_sentence[field] != null && _sentence[field].length() > 0) {
			if (_sentence[field].equals("A")) {
				result = true;
			} else if (_sentence[field].equals("V")) {
				result = false;
			}
		}
		return result;
	}

	public double getDouble(int field) {
		double result = 0.0;
		if (_sentence[field] != null && _sentence[field].length() > 0) {
			result = Double.valueOf(_sentence[field]).doubleValue();
		}
		return result;
	}

	public String getString(int field) {
		String result = new String();
		if (_sentence[field] != null && _sentence[field].length() > 0) {
			result = _sentence[field];
		}
		return result;
	}

	public void setString(int field, String value) {
		_sentence[field] = new String(value);
	}

	public boolean isChecksum() {
		if (_checksum == null || _checksum.length() == 0) {
			return false;
		}
		byte c1 = (byte) hexValue(_checksum);
		byte c2 = checksum();
		return (c1 == c2) ? true : false;
	}

	public void setChecksum() {
		byte c = checksum();
		_checksum = hexFormat(c, 2);
	}

	private static String hexFormat(int i, int j) {
		String s = Integer.toHexString(i);
		return padHex(s, j) + s.toUpperCase();
	}

	private static String padHex(String s, int i) {
		StringBuffer tmpBuffer = new StringBuffer();
		if (s.length() < i) {
			for (int j = 0; j < i - s.length(); j++) {
				tmpBuffer.append('0');
			}
		}
		return tmpBuffer.toString();
	}

	public Date getTime(int field) {
		Date result = new Date(0);
		String str = _sentence[field];
		if (str != null && str.length() >= 6) {
			int hour = Integer.parseInt(str.substring(0, 2));
			int minute = Integer.parseInt(str.substring(2, 4));
			int second = Integer.parseInt(str.substring(4, 6));
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			calendar.set(Calendar.YEAR, 0);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			calendar.set(Calendar.HOUR, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, second);
			result = calendar.getTime();
		}
		return result;
	}

	public Date getDate(int field) {
		Date result = new Date();
		String str = _sentence[field];
		if (str != null && str.length() >= 6) {
			int day = Integer.parseInt(str.substring(0, 2));
			int month = Integer.parseInt(str.substring(2, 4));
			int year = Integer.parseInt(str.substring(4, 6));
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			calendar.set(Calendar.YEAR, year + 2000);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			result = calendar.getTime();
		}
		return result;
	}

	public Date getDateTime(int dateField, int timeField) {
		Date result = new Date();
		String strTime = _sentence[timeField];
		String strDate = _sentence[dateField];
		if (strDate != null && strDate.length() >= 6 && strTime != null
				&& strTime.length() >= 6) {
			int day = Integer.parseInt(strDate.substring(0, 2));
			int month = Integer.parseInt(strDate.substring(2, 4));
			int year = Integer.parseInt(strDate.substring(4, 6));
			int hour = Integer.parseInt(strTime.substring(0, 2));
			int minute = Integer.parseInt(strTime.substring(2, 4));
			int second = Integer.parseInt(strTime.substring(4, 6));
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			calendar.set(Calendar.YEAR, year + 2000);
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, second);
			result = calendar.getTime();
		}
		return result;
	}

	public Angle getAngle(int field) {
		Angle result = new Angle();
		String str = _sentence[field];
		if (str != null && str.length() > 0) {
			int index = str.indexOf('.');
			if (index != -1) {
				String value = str.substring(0, index);
				int units = Integer.parseInt(value);
				result.degrees = (units / 100);
				result.minutes = (units - result.degrees * 100);
				value = str.substring(index + 1, str.length());
				double fraction = Double.valueOf("0." + value).doubleValue();
				result.seconds = (int) (fraction * 60);
				result.centiSeconds = (int) ((fraction * 60 - result.seconds) * 100.0);
			}
		}
		return result;
	}

	public byte checksum() {
		byte checksum = 0;
		for (int i = 1; i < _strSentence.length(); i++) {
			checksum ^= (byte) _strSentence.charAt(i);
		}
		return checksum;
	}

	public boolean isNorthing(int field) {
		boolean result = false;
		String str = _sentence[field];
		if (str != null && str.length() > 0) {
			if (str.equals("N"))
				result = true;
			else if (str.equals("S"))
				result = false;
		}
		return result;
	}

	public boolean isEasting(int field) {
		boolean result = false;
		String str = _sentence[field];
		if (str != null && str.length() > 0) {
			if (str.equals("E"))
				result = true;
			else if (str.equals("W"))
				result = false;
		}
		return result;
	}

	public class Angle {

		public int degrees;
		public int minutes;
		public int seconds;
		public int centiSeconds;
	}

	public class Latitude {

		public Angle value;
		public boolean northing;
	}

	public class Longitude {

		public Angle value;
		public boolean easting;
	}

	public Longitude getLongitude(int field, int eastingField) {
		Longitude result = new Longitude();
		result.value = getAngle(field);
		result.easting = isEasting(eastingField);
		return result;
	}

	public Latitude getLatitude(int field, int northingField) {
		Latitude result = new Latitude();
		result.value = getAngle(field);
		result.northing = isNorthing(northingField);
		return result;
	}

	static int angleToHundrethsOfSeconds(Angle angle) {
		int result = (angle.degrees * 360000) + (angle.minutes * 6000)
				+ (angle.seconds * 100) + angle.centiSeconds;
		return result;
	}

	static double angleToDegrees(Angle angle) {
		double result = (angle.degrees) + ((angle.minutes) / 60.00)
				+ ((angle.seconds) / 3600.00)
				+ ((angle.centiSeconds) / 360000.00);
		return result;
	}

	static double angleToRadian(Angle angle) {
		double result = angleToDegrees(angle);
		return (result * Math.PI / 180.00);
	}

	static int bearingToHundrethsOfDegrees(double value) {
		int digits = (int) value;
		int hundreths = (int) ((value - digits) * 100);
		return digits * 100 + hundreths;
	}

	protected static int hexValue(String hexaString) {
		int length = hexaString.length();
		int result = 0;
		int factor = 1;
		for (int i = length - 1; i >= 0; i--) {
			char c = hexaString.charAt(i);
			result += (hexDigitToInt(c) * factor);
			factor *= 16;
		}
		return result;
	}

	protected static int hexDigitToInt(char c) {
		int result = 0;
		if ((c >= 'a') && (c <= 'f'))
			result = c - 'a' + 10;
		else if ((c >= 'A') && (c <= 'F'))
			result = c - 'A' + 10;
		else if ((c >= '0') && (c <= '9'))
			result = c - '0';
		return result;
	}
}