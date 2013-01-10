package org.avm.device.gsm;

public class GsmResponse {

	public static final int KO = 0;

	public static final int OK = 1;

	public int status;

	public String value;

	public GsmResponse(int status, String value) {
		this.status = status;
		this.value = value;
	}

	public String toString() {
		return ((status == OK) ? "OK" : "KO") + " : " + this.value;
	}

}
