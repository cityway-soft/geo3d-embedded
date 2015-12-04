package org.avm.device.vtc1010.watchdog.state;

public interface WatchdogStateMachine {

	void startTimer();

	void stopTimer();

	void shutdownCallback();

}
