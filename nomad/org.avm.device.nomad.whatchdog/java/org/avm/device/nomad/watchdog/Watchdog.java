package org.avm.device.nomad.watchdog;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.nomad.watchdog.bundle.Activator;
import org.avm.device.nomad.watchdog.jni.WIRMA_IO;
import org.avm.device.nomad.watchdog.state.WatchdogStateMachine;
import org.avm.device.nomad.watchdog.state.WatchdogStateMachineContext;
import org.osgi.service.startlevel.StartLevel;

import EDU.oswego.cs.dl.util.concurrent.ClockDaemon;

public class Watchdog implements Runnable, WatchdogStateMachine {

	private Thread _thread;

	private Logger _log;

	private static WatchdogStateMachineContext _context;

	private static final String DEVICE = "/dev/ioc5";

	private static final long DELAY = 8 * 60 * 1000;

	private static final int EXIT_CODE = 2;

	private static final int START_LEVEL = 1;

	private ClockDaemon _timer;

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

		int handle = WIRMA_IO.dio_open(DEVICE);

		_log.debug("[DSU] Watchdog.run()");
		while (_thread != null && !_thread.isInterrupted()) {
			
			int value = WIRMA_IO.dio_read_input(handle);
			
			if (!_thread.isInterrupted()) {
				if (value == 0) {
					_log.debug("[DSU] call Watchdog.powerDown()");
					_context.powerDown();
				} else {
					_log.debug("[DSU] call Watchdog.powerUp()");
					_context.powerUp();
				}
			}
		}
		
		WIRMA_IO.dio_close(handle);

	}

	protected void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	public void startTimer() {
		_log.debug("[DSU] Watchdog.startTimer()");
		_handle = _timer.executeAfterDelay(DELAY, _task);
	}

	public void stopTimer() {
		_log.debug("[DSU] Watchdog.stopTimer()");
		if (_handle != null) {
			ClockDaemon.cancel(_handle);
			_handle = null;
		}
	}

	public void shutdownCallback() {
		_log.debug("[DSU] Watchdog.shutdownCallback()");
		_timer.shutDown();
		stop();
		StartLevel service = Activator.getStartLevelService();
		service.setStartLevel(START_LEVEL);
		long now = System.currentTimeMillis();
		while (service.getStartLevel() != START_LEVEL) {
			sleep(1000);
			if (System.currentTimeMillis() - now > 30 * 1000) {
				_log
						.debug("[DSU] Watchdog.shutdownCallback() timeout !!!!!!!!!!!!");
				break;
			}
		}

		System.exit(EXIT_CODE);
	}
}
