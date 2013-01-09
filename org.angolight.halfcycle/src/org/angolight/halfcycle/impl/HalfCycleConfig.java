package org.angolight.halfcycle.impl;

public interface HalfCycleConfig {

	public final static String MINIMUM_SPEED_UP_TAG = "minimum-speed-up";
	public final static String MINIMUM_SPEED_DOWN_TAG = "minimum-speed-down";
	public final static String POSITIVE_ACCELERATION_UP_TAG = "positive-acceleration-up";
	public final static String POSITIVE_ACCELERATION_DOWN_TAG = "positive-acceleration-down";
	public final static String NEGATIVE_ACCELERATION_UP_TAG = "negative-acceleration-up";
	public final static String NEGATIVE_ACCELERATION_DOWN_TAG = "negative-acceleration-down";
	public final static String FILENAME_TAG = "filename";

	public double getMinimumSpeedUp();

	public void setMinimumSpeedUp(double speed);

	public double getMinimumSpeedDown();

	public void setMinimumSpeedDown(double speed);

	public double getPositiveAccelerationUp();

	public void setPositiveAccelerationUp(double acceleration);

	public double getPositiveAccelerationDown();

	public void setPositiveAccelerationDown(double acceleration);

	public double getNegativeAccelerationUp();

	public void setNegativeAccelerationUp(double acceleration);

	public double getNegativeAccelerationDown();

	public void setNegativeAccelerationDown(double acceleration);

	public String getFilename();

	public void setFilename(String filename);

}
