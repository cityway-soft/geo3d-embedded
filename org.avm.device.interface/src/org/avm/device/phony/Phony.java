package org.avm.device.phony;



public interface Phony {
	public static final String PHONY_CONFIGURATION = "conducteur";
	public static final String LISTEN_CONFIGURATION = "conducteur-ecoute-discrete";

	void hangup() throws Exception;

	void dial(String string) throws Exception;

	void dialListenMode(String string) throws Exception;

	void answer() throws Exception;

	void setVolume(int val);

	int getDefaultSoundVolume();

	void setRingVolume(int volume);

}
