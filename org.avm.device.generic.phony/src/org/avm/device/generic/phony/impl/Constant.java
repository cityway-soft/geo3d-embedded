package org.avm.device.generic.phony.impl;

public interface Constant extends org.avm.device.gsm.Constant {

	public static final String AT_DIAL_BEGIN = "ATD ";
	public static final String AT_DIAL_END = ";\r";
	public static final String AT_HANGUP = "ATH0\r";
	public static final String AT_ANSWER = "ATA\r";
	public static final String BUSY = "BUSY";
	public static final String NO_DIALTONE = "NO DIALTONE";
	public static final String[] DIAL_ERROR = { "ERROR", BUSY, NO_DIALTONE };

}
