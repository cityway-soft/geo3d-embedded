package org.avm.device.generic.can.manager;

import java.util.Dictionary;
import java.util.Properties;

public interface CanManagerConfig {
	
	public static final String PROTOCOL_NAME_TAG = "protocol.name";
	
	public void add(Properties p);

	public void remove(String name);

	public Properties get(String name);
	
	public Dictionary get();
	
}
