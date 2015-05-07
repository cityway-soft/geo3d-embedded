package org.avm.elementary.dnssd;

public interface DnsSdServiceConfig {


	public String getAddress();

	public void setAddress(String address);

	public Integer getPort();

	public void setPort(Integer port);

	public Integer getPeriod();

	public void setPeriod(Integer period);

}
