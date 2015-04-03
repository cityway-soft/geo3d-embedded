package org.avm.hmi.swt.application.display;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

public class AVMDisplayImpl implements AVMDisplay, Runnable {

	private Display _display;

	private Object lock = new Object();

	private Thread _thread;

	private boolean initialized = false;

	private boolean running = false;

	private int failure;

	private static Logger logger = Logger.getInstance(AVMDisplayImpl.class);

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
//		PoorWatchDog wd = PoorWatchDog.getInstance();
//		File file = new File(System.getProperty("org.avm.home")
//				+ "/freeze.ignore");
//		if (file.exists()) {
//			logger.info("Freeze management ignore");
//		} else {
//			logger.info("Freeze management ACTIVATED.");
//			wd.start();
//
//		}
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
//				wd.reset();
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

//	static class PoorWatchDog {
//		private long t0 = -1;
//
//		private long timeout = 2000;
//
//		private Thread thread;
//
//		private int count = 0;
//
//		boolean wdrunning = false;
//
//		private Object wdlock = new Object();
//
//		private static PoorWatchDog instance;
//
//		public static PoorWatchDog getInstance() {
//			if (instance == null) {
//				instance = new PoorWatchDog();
//			}
//			return instance;
//		}
//
//		public void stop() {
//			wdrunning = false;
//			if (thread != null) {
//				thread.interrupt();
//				thread = null;
//			}
//		}
//
//		public void start() {
//			if (thread == null) {
//				wdrunning = true;
//				thread = new Thread(new Runnable() {
//
//					public void run() {
//						while (wdrunning && thread.isInterrupted() == false) {
//							long now = System.currentTimeMillis();
//							if ((now - t0) > timeout) {
//								count++;
//							} else {
//								if (count > 1){
//									logger.warn("OK mais Blocage SWT : "+ count);
//								}
//								count = 0;
//							}
//
//							if (count > 5) {
//								SimpleDateFormat df = new SimpleDateFormat(
//										"yyyy-MM-dd_HHmmss");
//								try {
//									FileOutputStream fos = new FileOutputStream(
//											System.getProperty("org.avm.home")
//													+ "/data/upload/freezeapp-"
//													+ df.format(new Date())
//													+ ".txt");
//									fos.close();
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
//								logger.warn("Forcage Exit application("
//										+ new Date() + ") !!!!!!!!!!!");
//								System.out.println("BLOCAGE SWT (" + new Date()
//										+ ")");
//								System.exit(1);
//							}
//							try {
//								synchronized (wdlock) {
//									wdlock.wait(1000);
//								}
//							} catch (InterruptedException e) {
//							}
//						}
//						System.out.println("Arret thread!!!!");
//
//					}
//
//				});
//			}
//			thread.start();
//		}
//
//		public void reset() {
//			if (wdrunning) {
//				t0 = System.currentTimeMillis();
//				synchronized (wdlock) {
//					wdlock.notify();
//				}
//			}
//
//		}
//
//	}

}