package org.avm.elementary.protocol.avm;

public class TimeoutException extends Exception {

	private static final long serialVersionUID = 1L;

	public TimeoutException() {
		super();
	}

	public TimeoutException(String detailMessage) {
		super(detailMessage);
	}

}
