package org.avm.device.generic.phony.impl;

public interface PhonyConfig {
	public static final String TAG_AT = "at.";
	public static final String TAG_CMD_REQUEST = "request";
	public static final String TAG_CMD_DESC = "desc";

	public static final String TAG_INIT = "init";

	public static final String TAG_SOUND_VOLUME = "defaultvolume";

	public static final String DESACTIVATE_LISTEN_MODE = "listen-mode-off";
	public static final String ACTIVATE_LISTEN_MODE = "listen-mode-on";

	public int getMaxLevelVolume();

	public int getDefaultSoundVolume();

	public void setDefaultSoundVolume(int volume);

	public void addSpecificAtCommand(String id, String at, String desc);

	public void removeSpecificAtCommand(String id);

	public String getSpecificCommand(String id);

	public void setInitAtCommand(String[] list);

	public String[] getInitAtCommand();
}
