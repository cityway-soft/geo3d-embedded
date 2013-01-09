package org.avm.device.sound;

public interface Sound {

	public static final String NAME = "name";
	public static final String URL = "url";
	public static final String PRIORITY = "priority";
	public static final String VOLUME = "volume";
	public static final String MAX_VOLUME = "max-volume";
	public static final String DEFAULT_CONFIGURATION = "default";

	public void setMasterVolume(short volume);

	public void configure(String name) throws Exception;
}
