package org.angolight.device.generic.leds;

import java.io.IOException;

public interface LedsDevice {

	public void open(String url) throws IOException;

	public boolean isOpen();

	public void close();

	public String send(String command) throws LedsException, IOException;

}
