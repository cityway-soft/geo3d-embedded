package org.avm.device.usbmass;

public interface UsbMassConfig {

	public static String TAG_MOUNT_POINT = "org.avm.device.usbmass.mount";

	public static String DEFAULT_MOUNT_POINT = System.getProperty(TAG_MOUNT_POINT, "/HardDisk");
	public static String TAG_POLL_FREQUENCY = "org.avm.device.usbmass.poll";

	public static String DEFAULT_POLL_FREQUENCY = System.getProperty(TAG_POLL_FREQUENCY, "5");
	
	public void setMountPoint(String mount);
	
	public String getMountPoint();
	
	public void setPollFrequency(String frequency);
	
	public String getPollFrequency();

}