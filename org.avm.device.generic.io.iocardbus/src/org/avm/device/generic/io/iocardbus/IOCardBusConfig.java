package org.avm.device.generic.io.iocardbus;

import org.avm.device.io.IOCardInfo;

public interface IOCardBusConfig {

	void add(String name, String category, String manufacturer, String model,
			String serial);

	void remove(String name);

	public IOCardInfo getIOCCardInfo(String name);

	public IOCardInfo[] getIOCCardInfos();

}
