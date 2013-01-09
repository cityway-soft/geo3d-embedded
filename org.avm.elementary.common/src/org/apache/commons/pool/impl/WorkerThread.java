package org.apache.commons.pool.impl;

public class WorkerThread extends Thread {

	private boolean _stopped;
	private boolean _running;
	private boolean _done;
	private Runnable _task;
	

	public WorkerThread(String name) {
		super(name);
	}

	public void run() {
		_running = true;
		while (!_stopped) {
			if (_done) {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						_stopped = true;
					}
				}
			} else {
				try {
					_task.run();
				} catch (Exception e) {
				} finally {
					_done = true;
				}
			}
		}
	}

	public synchronized void execute(Runnable task) {
		_task = task;
		_done = false;
		if (!_running) {
			setDaemon(true);
			start();
		} else {
			notifyAll();
		}
	}

	public synchronized boolean isRunning() {
		return _running;
	}

	public void setStopped(boolean stopped) {
		_stopped = stopped;
	}

}
