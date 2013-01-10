package org.avm.device.road.watchdog.state;

public interface WatchdogStateMachine {

	void startTimer();

	void stopTimer();

	void sleep();

	void shutdown();

	boolean wakeUpRTC();

	void wakeUp();

}
