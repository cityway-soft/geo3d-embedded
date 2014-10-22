package org.avm.elementary.wifi;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.Scheduler;

public class WifiManagerStateMachine {

	private WifiManagerStateMachineContext _fsm;
	private org.avm.device.wifi.Wifi _peer;
	private Logger _log;
	private Scheduler _scheduler;

	private int _timeout = 60;
	private Object _disconnectTimerTaskId;

	public WifiManagerStateMachine(org.avm.device.wifi.Wifi wifi) {
		_fsm = new WifiManagerStateMachineContext(this);
		_peer = wifi;
		_log = Logger.getInstance(this.getClass());
		_fsm.setDebugFlag(_log.getPriority() == Priority.DEBUG);
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
		}
		try {
			_peer.connect();
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	public void stopWifi() {
		if (_disconnectTimerTaskId == null) {
			_log.debug("Disconnect in " + _timeout + " secondes.");
			_disconnectTimerTaskId = _scheduler.schedule(
					new DisconnectTimerTask(), _timeout * 1000);
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
				_disconnectTimerTaskId = null;
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}

	}

}
