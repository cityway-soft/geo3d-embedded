package org.avm.device.phony;

public class PhoneEvent {
	
	public static final int READY = 0;

	public static final int RING = 1;
	
	public static final int BUSY = 2;

	public static final int NO_CARRIER = 3;

	public static final int ON_LINE = 4;

	public static final int DIALING = 5;

	public static final int ERROR = 6;

	public static final int CONTACT_LIST_CHANGED = 7;
	
	public static final int MODEM_NOT_AVAILABLE = 8;

	
	public static final PhoneEvent READY_PHONE_EVENT = new PhoneEvent(0);

	public static final PhoneEvent BUSY_PHONE_EVENT = new PhoneEvent(2);

	public static final PhoneEvent NO_CARRIER_PHONE_EVENT = new PhoneEvent(3);

	public static final PhoneEvent ON_LINE_PHONE_EVENT = new PhoneEvent(4);

	public static final PhoneEvent DIALING_PHONE_EVENT = new PhoneEvent(5);

	public static final PhoneEvent ERROR_PHONE_EVENT = new PhoneEvent(6);

	public static final PhoneEvent CONTACT_LIST_CHANGED_PHONE_EVENT = new PhoneEvent(7);
	
	public static final PhoneEvent MODEM_NOT_AVAILABLE_PHONE_EVENT = new PhoneEvent(8);


	private int _status;

	public PhoneEvent(int status) {
		_status = status;
	}

	public int getStatus() {
		return _status;
	}

	public String toString() {
		return "PhoneEvent [Status : " + _status + "]";
	}
}