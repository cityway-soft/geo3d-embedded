package org.avm.device.nomad.watchdog.state;

public interface WatchdogStateMachine {

	void startTimer();

	void stopTimer();

	void shutdownCallback();

}
