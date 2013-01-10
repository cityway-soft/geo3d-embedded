package org.avm.device.linux.sound;

import java.util.Dictionary;
import java.util.Properties;

public interface SoundConfig {

	void add(Properties properties);

	void remove(String name);

	public Properties get(String name);

	public Dictionary get();
	
	short getMaxVolume();
	
	void setMaxVolume(short value);
	
}
