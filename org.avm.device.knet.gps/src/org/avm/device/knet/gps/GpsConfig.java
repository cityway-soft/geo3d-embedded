package org.avm.device.knet.gps;

public interface GpsConfig {
	public static String DELAY_TAG = "delay";

	public static Double DEFAULT_DELAY = new Double(2);

	public static String CORRECT_TAG = "correct";

	public static Boolean DEFAULT_CORRECT = Boolean.FALSE;

	public Boolean getCorrect();

	public void setCorrect(Boolean correct);

	public Double getDelay();

	public void setDelay(Double delay);
}
