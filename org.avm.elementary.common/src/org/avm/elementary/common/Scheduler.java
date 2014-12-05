package org.avm.elementary.common;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

public class Scheduler {

	// pooled executor
	// -------------------------------------------------------------//

	private static PooledExecutor _executor;

	public void execute(Runnable task) {
		try {
			getExecutor().execute(task);
		} catch (InterruptedException e) {

		}
	}
	
	private Executor getExecutor() {
		if (_executor == null) {
			ThreadFactory factory = new DefaultThreadFactory();
			_executor = new PooledExecutor();
			_executor.setThreadFactory(factory);
			_executor.setMinimumPoolSize(1);
			_executor.setKeepAliveTime(60 * 1000);
		}
		return _executor;
	}

	private static class DefaultThreadFactory implements ThreadFactory {
		private int _count = 0;

		public Thread newThread(Runnable command) {
			Thread thread = new Thread(command);
			thread.setDaemon(true);
			thread.setName("[AVM] scheduler executor : " + _count++);
			return thread;
		}
	}

	// scheduler -------------------------------------------------------------//

	private Timer _scheduler = new Timer(true);

	public void cancel(Object handle) {
		if(handle instanceof TimerTask){
			TimerTask task = (TimerTask) handle;
			task.cancel();
		}
	}

	public Object schedule(Runnable task, long delay) {
		TimerTask handle = new DefaultTimerTask(task);
		_scheduler.schedule(handle, delay);
		return handle;
	}

	public Object schedule(Runnable task, Date when) {
		TimerTask handle = new DefaultTimerTask(task);
		_scheduler.schedule(handle, when);
		return handle;
	}

	public Object schedule(Runnable task, long period, boolean now) {
		TimerTask handle = new DefaultTimerTask(task);
		_scheduler.schedule(handle, now ? 0 : period, period);
		return handle;
	}

	public static void shutdown() {

	}

	private class DefaultTimerTask extends TimerTask {

		private Runnable _task;

		public DefaultTimerTask(Runnable task) {
			super();
			_task = task;
		}

		public void run() {
			try {
				_task.run();
			} catch (Throwable e) {

			}
		}
	}

	

	// private static ClockDaemon _scheduler;
	//
	// private ClockDaemon getScheduler() {
	// if (_scheduler == null) {
	// ThreadFactory factory = new DefaultThreadFactory();
	// _scheduler = new ClockDaemon();
	// _scheduler.setThreadFactory(factory);
	// }
	// return _scheduler;
	// }
	//
	// public void cancel(Object id) {
	// ClockDaemon.cancel(id);
	// }
	//
	// public Object schedule(Runnable task, long delay) {
	// ClockDaemon scheduler = getScheduler();
	// Object result = scheduler.executeAfterDelay(delay, task);
	// return result;
	// }
	//
	// public Object schedule(Runnable task, Date when) {
	// ClockDaemon scheduler = getScheduler();
	// Object result = scheduler.executeAt(when, task);
	// return result;
	// }
	//
	// public Object schedule(Runnable task, long period, boolean now) {
	// ClockDaemon scheduler = getScheduler();
	// Object result = scheduler.executePeriodically(period, task, now);
	// return result;
	// }
	//
	// public static void shutdown() {
	// if (_scheduler != null) {
	// _scheduler.shutDown();
	// _scheduler = null;
	// }
	// }

}
