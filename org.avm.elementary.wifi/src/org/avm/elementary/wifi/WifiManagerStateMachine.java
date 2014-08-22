package org.avm.elementary.wifi;

import org.apache.log4j.Logger;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.Scheduler;
import org.osgi.util.measurement.State;

public class WifiManagerStateMachine {

	private WifiManagerStateMachineContext _fsm;
	private org.avm.device.wifi.Wifi _peer;
	private Logger _log;
	private Scheduler _scheduler;

	private int _timeout = 60;
	private Object _disconnectTimerTaskId;
	private ProducerManager producer;

	public WifiManagerStateMachine(org.avm.device.wifi.Wifi wifi) {
		_fsm = new WifiManagerStateMachineContext(this);
		_peer = wifi;
		_log = Logger.getInstance(this.getClass());
		_scheduler = new Scheduler();
	}

	public void setTimeout(int timeout) {
		_timeout = timeout;
	}

	public void startWifi() {
		_log.debug("timer=" + _scheduler);
		if (_disconnectTimerTaskId != null) {
			_log.debug("Cancel timer disconnect.");
			_scheduler.cancel(_disconnectTimerTaskId);
			_disconnectTimerTaskId = null;
		} else {
			try {
				_peer.connect();
				publish(new State(1, "wifi"));
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
				producer.publish(new State(0, "wifi"));
			}
		}
	}

	public void stopWifi() {
		publish(new State(0, "wifi"));
		if (_disconnectTimerTaskId == null) {
			_log.debug("Disconnect in " + _timeout + " secondes.");
			_disconnectTimerTaskId = _scheduler.schedule(
					new DisconnectTimerTask(), _timeout * 1000);
		}
	}

	private void publish(State state) {
		if (producer != null) {
			producer.publish(state);
		} else {
			_log.error("Producer for state is not set!");
		}

	}

	public void entryWifiZone() {
		_fsm.entryWifiZone();
	}

	public void exitWifiZone() {
		_fsm.exitWifiZone();
	}

	class DisconnectTimerTask implements Runnable {

		public void run() {
			try {
				_peer.disconnect();
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}

	}

}
