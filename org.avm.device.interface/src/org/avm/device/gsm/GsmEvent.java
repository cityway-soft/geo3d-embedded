package org.avm.device.gsm;

import java.util.EventObject;

public class GsmEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public static final int RING = 0;

	public static final int HANGUP = 1;

	public static final int INCOMMING_SMS = 2;

	public static final int MODEM_OPENED = 3;

	public static final int MODEM_CLOSED = 4;

	public int type;

	public String value;

	public GsmEvent(Object source, int type) {
		this(source, type, null);
	}

	public GsmEvent(Object source, int type, String value) {
		super(source);
		this.type = type;
		this.value = value;
	}

	public String toString(int type) {
		String result = null;
		switch (type) {
		case RING:
			result = "RING";
			break;
		case HANGUP:
			result = "HANGUP";
			break;
		case INCOMMING_SMS:
			result = "INCOMMING SMS";
			break;
		case MODEM_OPENED:
			result = "MODEM_OPENED";
			break;

		case MODEM_CLOSED:
			result = "MODEM_CLOSED";
			break;
		default:
			result = "";
			break;
		}
		return result;
	}

	public String toString() {
		return toString(type) + " = " + value;
	}

}
