package org.avm.device.gsm;

import java.io.IOException;

public interface Gsm {

	public void send(GsmRequest command) throws GsmException,
			InterruptedException, IOException;

	public int getSignalQuality();

	public boolean isGprsAttached();

	public boolean isGsmAttached();

}
