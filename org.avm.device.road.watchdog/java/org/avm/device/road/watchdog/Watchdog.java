package org.avm.device.road.watchdog;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.road.klkpic.jni.KLK_PIC;
import org.avm.device.road.watchdog.bundle.Activator;
import org.avm.device.road.watchdog.state.WatchdogStateMachine;
import org.avm.device.road.watchdog.state.WatchdogStateMachineContext;
import org.avm.elementary.common.Scheduler;
import org.osgi.service.startlevel.StartLevel;

public class Watchdog implements WatchdogStateMachine {

	private static final String DEVICE = "/dev/klk-pic";
	private static final int EXIT_CODE = 2;
	private static final long WATCHDOG_DELAY = 8 * 60 * 1000;
	// private static final long WATCHDOG_DELAY = 30 * 1000;
	private static final long WATCHDOG_PERIOD = 3 * 1000;
	private static final long KEEP_ALIVE_PERIOD = 180 * 1000;
	private static final int MIN_START_LEVEL = 2;
	private static final int MEDIUM_START_LEVEL = 8;
	private static final int MAX_START_LEVEL = 10;

	private static WatchdogStateMachineContext _context;
	private Scheduler _scheduler;
	private KeepAliveTask _keepaliveTask;
	private ReadAPCTask _readAPCTask;
	private WatchdogTask _watchdogTask;
	private Object _keepaliveTaskId;
	private Object _readAPCTaskId;
	private Object _watchdogTaskId;
	private Logger _log = Logger.getInstance(this.getClass());

	public Watchdog() {
		_log.setPriority(Priority.INFO);
		_scheduler = new Scheduler();
		_context = new WatchdogStateMachineContext(this);
	}

	public void start() {
		_keepaliveTask = new KeepAliveTask();
		_keepaliveTaskId = _scheduler.schedule(_keepaliveTask,
				KEEP_ALIVE_PERIOD, true);
		_readAPCTask = new ReadAPCTask();
		_readAPCTaskId = _scheduler.schedule(_readAPCTask, WATCHDOG_PERIOD,
				true);
	}

	public void stop() {
		if (_readAPCTaskId != null) {
			_scheduler.cancel(_readAPCTaskId);
			_readAPCTaskId = null;
		}
		if (_keepaliveTaskId != null) {
			_scheduler.cancel(_keepaliveTaskId);
			_keepaliveTaskId = null;
		}
	}

	public void startTimer() {
		_log.debug("[DSU] start timer");
		_watchdogTask = new WatchdogTask();
		_watchdogTaskId = _scheduler.schedule(_watchdogTask, WATCHDOG_DELAY);
	}

	public void stopTimer() {
		if (_watchdogTaskId != null) {
			_log.debug("[DSU] stop timer");
			_scheduler.cancel(_watchdogTaskId);
			_watchdogTaskId = null;
		}
	}

	public boolean wakeUpRTC() {
		int handle = KLK_PIC.klkpic_open(DEVICE);
		int value = KLK_PIC.klkpic_read_wakeup(handle);
		KLK_PIC.klkpic_close(handle);
		_log.debug("[DSU] pic wakeup " + Integer.toHexString(value));
		return ((value & 0x0400) == 0x0400) ? true : false;
		// return ((value & 0x10) == 0x10) ? false : true;
	}

	public void shutdown() {
		_log.info("[DSU] call shutdown");
		stop();
		StartLevel service = Activator.getStartLevelService();
		service.setStartLevel(MIN_START_LEVEL);
		long now = System.currentTimeMillis();
		while (service.getStartLevel() != MIN_START_LEVEL) {
			sleep(1000);
			if (System.currentTimeMillis() - now > 60 * 1000) {
				break;
			}
		}
		_log.info("[DSU] call system exit !");
		System.exit(EXIT_CODE);
	}

	public void sleep() {
		_log.info("[DSU] call sleep");
		StartLevel service = Activator.getStartLevelService();
		service.setStartLevel(MEDIUM_START_LEVEL);
		long now = System.currentTimeMillis();
		while (service.getStartLevel() != MEDIUM_START_LEVEL) {
			sleep(3000);
			_log.info("[DSU] wait all service stoped");
			if (System.currentTimeMillis() - now > 60 * 1000) {
				_log.info("[DSU] timeout change start level");
				break;
			}
		}

		String[] array = { "sh", "-c", "wakeup off" };
		try {
			_log.info("[DSU] last trace ----------------------------------------------------------------");
			Process process = Runtime.getRuntime().exec(array);
			process.waitFor();
			_log.info("[DSU] first trace ---------------------------------------------------------------");
		} catch (Exception e) {
		}

	}

	public void wakeUp() {
		_log.info("[DSU] call wakup");
		String[] array = { "sh", "-c", "wakeup on" };
		try {
			Process process = Runtime.getRuntime().exec(array);
			process.waitFor();
		} catch (Exception e) {
		}

		StartLevel service = Activator.getStartLevelService();
		service.setStartLevel(MAX_START_LEVEL);
		long now = System.currentTimeMillis();
		while (service.getStartLevel() != MAX_START_LEVEL) {
			sleep(1000);
			_log.info("[DSU] wait all service started");
			if (System.currentTimeMillis() - now > 60 * 1000) {
				_log.info("[DSU] timeout change start level");
				break;
			}
		}
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	class KeepAliveTask implements Runnable {
		public void run() {
			// KLK_PIC.klkpic_keep_alive();

			_log.debug("[DSU] call keep alive");
			FileWriter writer = null;
			try {
				writer = new FileWriter("/dev/watchdog");
				writer.write("1");
			} catch (IOException e) {
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {

					}
				}
			}

		}
	}

	class ReadAPCTask implements Runnable {
		public void run() {
			int handle = KLK_PIC.klkpic_open(DEVICE);
			int value = KLK_PIC.klkpic_read_apc(handle);
			KLK_PIC.klkpic_close(handle);
			_log.debug("[DSU] pic state " + Integer.toHexString(value));
			if ((value & 0x0400) == 0x0400) {
				_context.powerDown();
			} else {
				_context.powerUp();
			}
		}
	}

	class WatchdogTask implements Runnable {
		public void run() {
			_context.timeout();
		}
	}
}
