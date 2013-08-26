package org.avm.business.core;

public interface AvmConfig {

	public Integer getAmplitude();

	public void setAmplitude(Integer amplitude);

	public Integer getChevauchement();

	public void setChevauchement(Integer amplitude);

	public Integer getMaxTranche();

	public void setMaxTranche(Integer amplitude);

	public Integer getToleranceDev();

	public void setToleranceDev(Integer tolerancedev);

	public Integer getSuiviCrsPeriod();

	public void setSuiviCrsPeriod(Integer suivicrsperiod);

	public void setCheckValidite(boolean equals);

	public boolean isCheckValidite();

	public String getAutomaticSALabel();

	public void setAutomaticSALabel(String label);
	
	public boolean isAutomaticCourseMode();

	public void setAutomaticCourseMode(boolean valid);
	

}
