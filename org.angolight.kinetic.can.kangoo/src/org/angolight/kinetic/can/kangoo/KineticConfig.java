package org.angolight.kinetic.can.kangoo;

public interface KineticConfig {
	
	public final static String CAN_SERVICE_PID_TAG = "can-service-pid";

	public final static String MEDIAN_FILTER_LENGTH_TAG = "median-filter-length";
	public final static String ACQUISITION_PERIOD_TAG = "acquisition-period";
	public final static String NOTIFICATION_PERIOD_TAG = "notification-period";

	public String getCanServicePid();

	public void setCanServicePid(String pid);
	
	public int getMedianFilterLength();

	public void setMedianFilterLength(int length);

	public double getAcquisitionPeriod();

	public void setAcquisitionPeriod(double period);

	public double getNotificationPeriod();

	public void setNotificationPeriod(double period);
	
	
}
