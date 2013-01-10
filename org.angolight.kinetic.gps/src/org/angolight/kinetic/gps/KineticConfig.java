package org.angolight.kinetic.gps;

public interface KineticConfig {

	public final static String MINIMUM_SPEED_UP_TAG = "minimum-speed-up";
	public final static String MINIMUM_SPEED_DOWN_TAG = "minimum-speed-down";

	public double getMinimumSpeedUp();

	public void setMinimumSpeedUp(double speed);

	public double getMinimumSpeedDown();

	public void setMinimumSpeedDown(double speed);

}
