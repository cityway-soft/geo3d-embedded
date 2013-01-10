package org.avm.elementary.media.avm;

public interface MediaAvmConfig {

	public String getMediaId();

	public String getAddress();

	public void setAddress(String address);

	public Integer getPort();

	public void setPort(Integer port);

	public Integer getPeriod();

	public void setPeriod(Integer period);

}
