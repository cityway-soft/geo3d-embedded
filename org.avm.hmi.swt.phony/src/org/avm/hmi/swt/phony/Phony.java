package org.avm.hmi.swt.phony;

public interface Phony {
	public static final int ATTACHEMENT_NONE = 0;
	public static final int ATTACHEMENT_GSM_OK = 1;
	public static final int ATTACHEMENT_GSM_GPRS_OK = 2;
	void call(String contactName) throws Exception;
	void setVolume(int value);
	void dial(String str);
	void answer();
	void hangup();
}
