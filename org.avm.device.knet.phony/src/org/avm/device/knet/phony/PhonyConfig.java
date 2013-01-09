package org.avm.device.knet.phony;

public interface PhonyConfig {
	public static String TAG_CONTACT_LIST = "org.avm.device.phony.contactlist";

	public static String DEFAULT_CONTACT_LIST = "";

	public static String TAG_RING_SOUND = "org.avm.device.phony.ringsound";

	public static int DEFAULT_RING_SOUND = 2;

	public void setContactList(String list);

	public String getContactList();
}
