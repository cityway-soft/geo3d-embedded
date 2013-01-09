/*
 * Created on 14 sept. 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.avm.device.generic.gps;

import java.util.Date;

/**
 * @author Daniel SURU (MERCUR)
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RMCSentence {

	private Date _date;
	private double _speed;
	private double _bearing;
	private NMEASentence.Longitude _longitude;
	private NMEASentence.Latitude _latitude;
	private boolean _valid;

	/**
	 * 
	 */
	public RMCSentence() {
		super();
	}

	public boolean parse(NMEASentence sentence) {
		_date = sentence.getDateTime(9, 1);
		_valid = sentence.getBoolean(2);
		_latitude = sentence.getLatitude(3, 4);
		_longitude = sentence.getLongitude(5, 6);
		_speed = sentence.getDouble(7);
		_bearing = sentence.getDouble(8);
		return true;
	}

	public double getBearing() {
		return _bearing;
	}

	public Date getDate() {
		return _date;
	}

	public NMEASentence.Latitude getLatitude() {
		return _latitude;
	}

	public NMEASentence.Longitude getLongitude() {
		return _longitude;
	}

	public double getSpeed() {
		return _speed;
	}

	public boolean isValid() {
		return _valid;
	}
}