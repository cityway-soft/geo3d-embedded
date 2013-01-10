package org.avm.device.wifi;

import java.util.Properties;

public interface Wifi {

	public void connect();

	public void disconnect();

	public boolean isConnected();

	public Properties getProperties();

}
