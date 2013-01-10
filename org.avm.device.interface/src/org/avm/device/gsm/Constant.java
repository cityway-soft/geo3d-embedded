package org.avm.device.gsm;

public interface Constant {

	public static final char CR = '\r';

	public static final char LF = '\n';

	public static final byte[] CRLF = { CR, LF };

	public static final byte CTRL_Z = 26;

	public static final String OK = "OK";

	public static final String READY = "READY";

	public static final String CPIN_READY = "+CPIN: READY";

	public static final String NO_CARRIER = "NO CARRIER";

	public static final String RING = "RING";

	public static final String CALLING_LINE_PRESENTATION = "+CLIP";

	public static final String SMS_INCOMMING = "+CMTI";

	public static final String[] ERROR = { "ERROR" };

	public static final String AT_AT = "AT\r";

	public static final String AT_ECHO_OFF = "ATE0\r";

	public static final String AT_SIGNAL_QUALITY = "AT+CSQ\r";

	public static final String AT_CHECK_PIN = "AT+CPIN?\r";

	public static final String AT_SET_PIN = "AT+CPIN=";
	
	public static final String AT_GPRS_ATTACHED = "AT+CGREG?\r";
	
	public static final String AT_GSM_ATTACHED = "AT+CREG?\r";

}
