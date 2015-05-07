package org.avm.elementary.media.mqtt;

public interface MediaMqttConfig {

	public String getMediaId();

	public String getAddress();

	public void setAddress(String address);

	public Integer getPort();

	public void setPort(Integer port);

	public Integer getPeriod();

	public void setPeriod(Integer period);

}
