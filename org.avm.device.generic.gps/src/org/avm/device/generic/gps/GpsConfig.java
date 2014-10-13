package org.avm.device.generic.gps;

public interface GpsConfig {

	public String getUrlConnection();

	public void setUrlConnection(String uri);

	public Boolean getCorrect();

	public void setCorrect(Boolean correct);
 
	public Double getDelay();

	public void setDelay(Double delay);
}
