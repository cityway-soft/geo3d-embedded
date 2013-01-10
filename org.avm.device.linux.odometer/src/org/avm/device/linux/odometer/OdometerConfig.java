package org.avm.device.linux.odometer;

public interface OdometerConfig {

	public double getCounterFactor();

	public void setCounterFactor(double factor);

	public double getSpeedLimit();

	public void setSpeedLimit(double speed);

	public double getTrackLimit();

	public void setTrackLimit(double track);

	public int getSampleLimit();

	public void setSampleLimit(int sample);

}
