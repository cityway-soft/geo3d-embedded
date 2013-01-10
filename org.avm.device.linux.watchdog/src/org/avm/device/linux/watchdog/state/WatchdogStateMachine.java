package org.avm.device.linux.watchdog.state;

public interface WatchdogStateMachine {

	void startTimer();

	void stopTimer();

	void shutdownCallback();

}
