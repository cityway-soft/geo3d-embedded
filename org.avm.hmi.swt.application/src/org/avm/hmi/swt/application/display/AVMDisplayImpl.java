package org.avm.hmi.swt.application.display;

import java.util.Date;

import org.eclipse.swt.widgets.Display;

public class AVMDisplayImpl implements AVMDisplay, Runnable {

	private Display _display;

	private Object lock = new Object();

	private Thread _thread;

	private boolean initialized = false;

	private boolean running = false;

	private int failure;

	public void start() {
		if (_thread == null) {
			running = true;
			_thread = new Thread(this);
			_thread.setName("[AVM] swt.application " + new Date());
			_thread.start();
		}
	}

	public void stop() {
		running = false;
		_thread.interrupt();
		_thread = null;
	}

	private void debug(String debug) {
		// if (DEBUG) {
		System.out.println("[AVMDisplay " + Thread.currentThread().getName()
				+ " DEBUG] " + debug);
		// }
	}

	private void info(String info) {
		System.out.println("[AVMDisplay " + Thread.currentThread().getName()
				+ " INFO] " + info);
	}

	private void error(String error) {
		System.out.println("[AVMDisplay " + Thread.currentThread().getName()
				+ " ERROR] " + error);
	}

	private void close() {
		debug("Disposing Display...");
		// if (!System.getProperty("os.name").equals("Windows CE")) {
		try {
			_display.dispose();
			initialized = false;
		} catch (Throwable t) {
			t.printStackTrace();
			_display = null;
		}
		// }
		debug("disposed! Display...");
	}

	private void createDisplay() {
		info("Display must be created...");
		while (_display == null) {
			try {
				info("Create default Display...");
				_display = Display.getDefault();
				if (_display != null) {
					Thread uiThread = Display.getCurrent().getThread();
					if (uiThread != Thread.currentThread()) {
						error("Current Display thread id not correct : dispose Display...");
						close();
					}
				} else {
					info("OK Display created...");
				}
			} catch (Throwable t) {
				error("handle not available (check export DISPLAY ?)");
				try {
					_display = null;
					Thread.sleep(5000);
				} catch (InterruptedException e) {

				}
			}
		}
	}

	/**
	 * This method initializes Shell
	 */
	private void open() {
		debug("Opening display...");
		synchronized (lock) {
			if (_display == null) {
				createDisplay();
			}
			initialized = true;
			lock.notifyAll();
			debug("unlocked...");
		}
		debug("Display opened (" + _display + ")");
		failure = 0;
		while (!_display.isDisposed() && running) {
			try {
				if (!_display.readAndDispatch())
					_display.sleep();
			} catch (Throwable t) {
				error("Oops !!! Throwable received in readAndDispatch ! ("
						+ Thread.currentThread().getName() + ")");
				failure++;
				if (failure > 100) {
					running = false;
				}
				t.printStackTrace();
			}
		}
		if (running == false) {

		}

		debug("Display = null");
	}

	public boolean isDisposed() {
		return _display.isDisposed();
	}

	public Display getDisplay() {
		if (initialized == false) {
			debug("getDisplay sync unlock...");
			synchronized (lock) {
				try {
					debug("Waiting for unlock ...");
					long t0 = System.currentTimeMillis();
					if (initialized == false) {
						lock.wait();
					}
					debug("...unlock done ("
							+ (System.currentTimeMillis() - t0) + ")");
				} catch (InterruptedException e) {
				}
			}
		}
		debug("(already initialized)");

		return _display;
	}

	public void run() {
		try {
			_display = null;
			open();
			close();

		} catch (Throwable t) {
			error("Oops !!! Throwable received when opening display!");
			t.printStackTrace();
		}
	}

}