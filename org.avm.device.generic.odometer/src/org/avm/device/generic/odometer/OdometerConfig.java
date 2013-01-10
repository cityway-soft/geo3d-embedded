package org.avm.device.generic.odometer;

public interface OdometerConfig {

	public Double getCounterFactor();

	public void setCounterFactor(Double factor);

	public Double getSpeedLimit();

	public void setSpeedLimit(Double limit);
}
