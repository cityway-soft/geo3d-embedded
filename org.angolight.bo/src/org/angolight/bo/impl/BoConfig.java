package org.angolight.bo.impl;

import java.io.IOException;

public interface BoConfig {
	public final static String VMIN_UP_TAG = "vminup";
	public final static String VMIN_DOWN_TAG = "vmindown";

	public final static double DEFAULT_VMIN_UP = (1.5 / 3.6);
	public final static double DEFAULT_VMIN_DOWN = (1 / 3.6);

	public final static String VMAX_UP_TAG = "vmaxup";
	public final static String VMAX_DOWN_TAG = "vmaxdown";

	public final static double DEFAULT_VMAX_UP = (90 / 3.6);
	public final static double DEFAULT_VMAX_DOWN = (85 / 3.6);

	public final static String TRIGGER_PERCENT_TAG = "trigger_percent";
	public final static double DEFAULT_TRIGGER_PERCENT = 5;

	public final static String CURVES_FILE_NAME_TAG = "curvers_filename";
	public final static String DEFAULT_CURVES_FILE_NAME = "{0}/data/bo/20060908150459/curves.txt";

	public void setVMin(Double vmin_up, Double vmin_down);

	public double getVMinUp();

	public double getVMinDown();

	public void setVMax(Double vmax_up, Double vmax_down);

	public double getVMaxDown();

	public double getVMaxUp();

	public double getTriggerPercent();

	public void setTriggerPercent(Double trigPercent);

	public String getCurvesFileName();

	public void setCurvesFileName(String filename) throws IOException;
}
