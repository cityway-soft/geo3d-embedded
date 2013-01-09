package org.avm.elementary.media.jms;

public interface MediaJMSConfig {

	public String getDestination();

	public void setDestination(String destination);

	public String getClientILService();

	public void setClientILService(String clientILService);

	public Integer getPingPeriod();

	public void setPingPeriod(Integer pingPeriod);

	public String getServerILFactory();

	public void setServerILFactory(String serverILFactory);

	public Integer getUilBufferSize();

	public void setUilBufferSize(Integer uilBufferSize);

	public Integer getUilChrunkSize();

	public void setUilChrunkSize(Integer uilChrunkSize);

	public Integer getUilPort();

	public void setUilPort(Integer uilPort);

	public Boolean getUilTCPNoDelay();

	public void setUilTCPNoDelay(Boolean uilTCPNoDelay);

	public String getMediaId();

	public String getUilAddress();

	public void setUilAddress(String uilAddress);

}
