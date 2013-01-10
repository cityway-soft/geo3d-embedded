package org.avm.elementary.common;

public interface Device extends org.osgi.service.device.Device {

	public static int MATCH_CATEGORY = 2;

	public static int MATCH_MODEL = 6;

	public static int MATCH_VERSION = 8;

	public static int MATCH_SERIAL = 10;

	public DeviceConfig getConfig();

}
