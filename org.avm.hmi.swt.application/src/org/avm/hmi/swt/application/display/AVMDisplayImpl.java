package org.avm.hmi.swt.application.display;

import java.util.HashMap;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class AVMDisplayImpl implements AVMDisplay, Runnable {

	private Display _display;

	private Object lock = new Object();

	private Thread _thread;

	private boolean initialized = false;

	public void start() {
		if (_thread == null) {
			_thread = new Thread(this);
			_thread.setName("[AVM] swt.application");
			_thread.start();
		}
	}

	public void stop() {
		_thread.interrupt();
		close();
		_thread = null;
	}

	private void debug(String debug) {
		if (DEBUG) {
			System.out.println(debug);
		}
	}

	private void close() {
		debug("[AVMDisplay] closing avmdisplay...");
		_display.asyncExec(new Runnable() {
			public void run() {
				debug("[AVMDisplay] disposing Display...");
				if (!System.getProperty("os.name").equals("Windows CE")) {
					try {
						_display.dispose();
						initialized = false;
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				debug("[AVMDisplay] disposed! Display...");
			}
		});

		
		debug("[AVMDisplay] avmdisplay closed.");

	}

	/**
	 * This method initializes sShell
	 */
	private void open() {
		debug("[AVMDisplay ] Opening display...");
		synchronized (lock) {
			if (_display == null) {
				_display = Display.getDefault();
			}
			initialized = true;
			lock.notifyAll();
			debug("[AVMDisplay ] unlocked...");
		}
		debug("Display opened (" + _display + ")");
		while (!_display.isDisposed()) {
			try {
				if (!_display.readAndDispatch())
					_display.sleep();
			} catch (Throwable t) {
				System.err
						.println("[AVMDisplay] OOOOOOOOOOOOOOOOOOOOOOOOOOppppps !!! Throwable received !");
				t.printStackTrace();
			}
		}
		_display = null;
		
		debug("Display = null");
	}

	public boolean isDisposed() {
		return _display.isDisposed();
	}

	public Display getDisplay() {
		if (initialized == false) {
			debug("[AVMDisplay ] getDisplay sync unlock...");
			synchronized (lock) {
				try {
					debug("[AVMDisplay ]Waiting for unlock ...");
					long t0 = System.currentTimeMillis();
					if (initialized == false) {
						lock.wait();
					}
					debug("[AVMDisplay]...unlock done ("
							+ (System.currentTimeMillis() - t0) + ")");
				} catch (InterruptedException e) {
				}
			}
		}
		debug("[AVMDisplay ] (already initialized)");

		return _display;
	}
	

	public void run() {
		try {
			open();
		} catch (Throwable t) {
			System.err
					.println("[AVMDisplay] OOOOOOOOOOOOOOOOOOOOOOOOOOppppps !!! Throwable received !");
			t.printStackTrace();
		}
	}

}
