package org.avm.elementay.can.logger;


public interface LoggerConfig {
	
	public static String FILENAME_TAG = "filename";
	
	public final static String CAN_SERVICE_PID_TAG = "can-service-pid";
	
	public String getCanServicePid();

	public void setCanServicePid(String pid);
	
	String getFilename();

	void setFilename(String filename);

}
