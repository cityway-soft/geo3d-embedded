/*
 * Created on 14 sept. 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.avm.device.generic.gps;

/**
 * @author Daniel SURU (MERCUR)
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GGASentence {

	private boolean _fix = false;
	private int _satellites = 0;
	private double _altitude = 0.0;

	/**
	 * 
	 */
	public GGASentence() {
		super();
	}

	public boolean parse(NMEASentence sentence) {
		_fix = sentence.getBoolean(6);
		_satellites = sentence.getInteger(7);
		_altitude = sentence.getDouble(9);
		return true;
	}

	public double get_altitude() {
		return _altitude;
	}

	public boolean get_fix() {
		return _fix;
	}

	public int get_satellites() {
		return _satellites;
	}
}