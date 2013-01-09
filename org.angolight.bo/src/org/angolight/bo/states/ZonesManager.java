package org.angolight.bo.states;

public interface ZonesManager {
	int getCurrentZone();

	int getNextZone();

	double getVmaxUp();

	double getVmaxDown();

	double getVminUp();

	double getVminDown();

	double getSpeed();

	void zoneVMax();

	void zoneUp();

	void zoneDown();

	void zoneInit();

	void resetTimer();

	void killTimer();

	void debug(String string);
}
