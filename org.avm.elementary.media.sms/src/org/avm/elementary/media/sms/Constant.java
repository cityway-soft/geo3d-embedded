package org.avm.elementary.media.sms;

public interface Constant extends org.avm.device.gsm.Constant {

	public static final String AT_SMS_MODE_TEXT = "AT+CMGF=1\r";

	public static final String AT_SMS_LIST_TEXT = "AT+CMGL=ALL\r";

	public static final String AT_SMS_MODE_BIN = "AT+CMGF=0\r";

	public static final String AT_SMS_LIST_BIN = "AT+CMGL=4\r";

	public static final String AT_SMS_SET_SMSC = "AT+CSCA=\"";

	public static final String AT_SMS_SMSC_PARAM = "AT+CSMP=17,167,0,0";

	public static final String AT_SMS_SET_RECEIVER = "AT+CMGS=";

	public static final String AT_SMS_DELETE_BANK = "AT+CMGD=";

	public static final String AT_SMS_READ_BANK = "AT+CMGR=";
	
	public static final String AT_SMS_USE_SIM_MEMORY= "AT+CPMS=\"SM\",\"SM\",\"SM\"\r";
	
	public static final String AT_SMS_GET_TOTAL_MEMORY= "AT+CPMS?\r";
	
	public static final String SMS_INCOMMING = "+CMTI";

	public static final String AT_SMS_INCOMMING = "AT+CNMI=2,1,0,0,1\r";

}
