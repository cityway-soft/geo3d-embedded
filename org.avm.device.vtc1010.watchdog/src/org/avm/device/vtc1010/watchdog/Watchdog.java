package org.avm.device.vtc1010.watchdog;

import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.vtc1010.common.api.Vtc1010APC;
import org.avm.device.vtc1010.watchdog.bundle.Activator;
import org.avm.device.vtc1010.watchdog.state.WatchdogStateMachine;
import org.avm.device.vtc1010.watchdog.state.WatchdogStateMachineContext;
import org.osgi.service.startlevel.StartLevel;

import EDU.oswego.cs.dl.util.concurrent.ClockDaemon;

public class Watchdog implements Runnable, WatchdogStateMachine {

	private Thread _thread;

	private Logger _log;

	private static WatchdogStateMachineContext _context;

	//private static final String DEVICE = "/dev/vsi0";

	private static final long DELAY = 4 * 60 * 1000;

	private static final int EXIT_CODE = 2;

	private static final int START_LEVEL = 1;

	private ClockDaemon _timer;
	
	private Vtc1010APC vtc1010apc = null;

	
	public void setVtc1010apc(Vtc1010APC vtc1010apc) {
		this.vtc1010apc = vtc1010apc;
	}

	private static Runnable _task = new Runnable() {
		public void run() {
			_context.shutdown();
		}
	};
	private Object _handle;

	public Watchdog() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);

		_timer = new ClockDaemon();
		_context = new WatchdogStateMachineContext(this);
	}

	public void start() {
		if (_thread == null) {
			_thread = new Thread(this);
			_thread.setName("[AVM] watchdog");
			_thread.setDaemon(true);
			_thread.start();
		}
	}

	public void stop() {
		if (_thread != null && !_thread.isInterrupted()) {
			_thread.interrupt();
			_thread = null;
		}
	}

	public void run() {

		
		try {
			int handle = vtc1010apc.open();
			while (_thread != null && !_thread.isInterrupted()) {
				int state  = vtc1010apc.readState(handle);
				System.out.println("Watchdog state "+ state);
				if (!_thread.isInterrupted()) {
					if (state == Vtc1010APC.APC_OFF) {
						System.out.println("Call Watchdog.powerDown()");
						_context.powerDown();
					} else {
						System.out.println("call Watchdog.powerUp()");
						_context.powerUp();
					}
				}
			}
			vtc1010apc.close(handle);
		} catch (Exception e) {
			_log.error(e);
		}
	}

	protected void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	public void startTimer() {
		_log.debug("Watchdog.startTimer()");
		_handle = _timer.executeAfterDelay(DELAY, _task);
	}

	public void stopTimer() {
		_log.debug("Watchdog.stopTimer()");
		if (_handle != null) {
			ClockDaemon.cancel(_handle);
			_handle = null;
		}
	}

	public void shutdownCallback() {
		_log.debug("Watchdog.shutdownCallback()");
		_timer.shutDown();
		stop();
		StartLevel service = Activator.getStartLevelService();
		service.setStartLevel(START_LEVEL);
		long now = System.currentTimeMillis();
		while (service.getStartLevel() != START_LEVEL) {
			sleep(1000);
			if (System.currentTimeMillis() - now > 30 * 1000) {
				_log
						.debug("Watchdog.shutdownCallback() timeout !!!!!!!!!!!!");
				break;
			}
		}

		System.exit(EXIT_CODE);
	}
}
