package org.avm.device.generic.can;

import java.util.List;

public interface CanConfig {

	public String getUrl();

	public void setUrl(String url);

	public String getFilter();

	public void setFilter(String filter);

	public String getMode();

	public void setMode(String mode);

	public int getBufferSize();

	public void setBufferSize(int bufferSize);

	public int getThreadPriority();

	public void setThreadPriority(int threadPriority);

	public long getSleepTime();

	public void setSleepTime(long sleepTime);

	public List getDriverFilters();

	public void setDriverFilters(List driverFilters);
	
	public String getUrlConnection();

}
