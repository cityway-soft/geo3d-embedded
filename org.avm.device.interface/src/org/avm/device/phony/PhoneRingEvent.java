/**
 * 
 */
package org.avm.device.phony;

/**
 * @author lbr
 * 
 */
public class PhoneRingEvent extends PhoneEvent {
	private String _callingNumber = null;

	public PhoneRingEvent(String callingNumber) {
		super(RING);
		_callingNumber = callingNumber;
	}

	public String getCallingNumber() {
		return _callingNumber;
	}

	public String toString() {
		return this.getClass().getName() + " _status : " + getStatus()
				+ " _callingNumber : " + _callingNumber;
	}

}
