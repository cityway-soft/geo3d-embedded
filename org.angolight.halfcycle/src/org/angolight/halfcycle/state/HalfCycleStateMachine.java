package org.angolight.halfcycle.state;

public interface HalfCycleStateMachine {

	double getMinimumSpeedUp();

	double getMinimumSpeedDown();

	double getPositiveAccelerationUp();

	double getPositiveAccelerationDown();

	double getNegativeAccelerationUp();

	double getNegativeAccelerationDown();

	void reset();

	void initilizeHalfCycleSpeed(double speed);

	void updatePositiveH1H2(double speed);

	void updateNegativeH1H2(double speed);

	void updatePositiveHalfCycle(double speed, double acceleration);

	void updateNegativeHalfCycle(double speed, double acceleration);

	void notifyHalfCycle();

}
