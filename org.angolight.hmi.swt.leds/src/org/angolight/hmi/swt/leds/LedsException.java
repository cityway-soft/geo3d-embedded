package org.angolight.hmi.swt.leds;

public class LedsException extends Exception {

	private static final long serialVersionUID = -1429022850245603376L;

	public static final String BUSY = "BUSY";
	public static final String ERROR = "ERROR";

	public LedsException() {
		super();
	}

	public LedsException(String message) {
		super(message);
	}

}
