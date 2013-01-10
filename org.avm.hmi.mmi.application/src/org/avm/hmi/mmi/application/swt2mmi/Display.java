/**
 * 
 */
package org.avm.hmi.mmi.application.swt2mmi;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.knet.mmi.Mmi;
import org.avm.device.knet.mmi.MmiDialogOut;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper;

/**
 * @author lbr
 */
public class Display {
	private static Display INSTANCE = null;
	private Mmi _mmi;
	private boolean _isOpened;
	private Thread _thread;
	private Object lock = new Object();
	private MmiEvent _currentMmiEvent;
	protected boolean _debug = true;
	private Logger _log;

	/**
	 * La présence d'un constructeur privé supprime le constructeur public par
	 * défaut.
	 */
	private Display() {
		_log = Logger.getInstance(this.getClass().getName());
		_log.setPriority(Priority.DEBUG);
	}

	/**
	 * Le mot-clé synchronized sur la méthode de création empêche toute
	 * instanciation multiple même par différents threads. Retourne l'instance
	 * du singleton.
	 */
	public synchronized static Display createInstance() {
		if (INSTANCE == null)
			INSTANCE = new Display();
		return INSTANCE;
	}

	public void open() {
		try {
			_log.debug("opening display...");
			_isOpened = true;
			_thread = new Thread(new DialogOutWatcher());
			_thread.start();
			_log.debug("display opened");
		} catch (Exception t) {
			_log.error("open ", t);
		}
	}

	public void close() {
		_log.debug("closing display...");
		_isOpened = false;
		_log.debug("display closed.");
	}

	private void setMmiEvent(MmiEvent event) {
		_log.debug("TRT de " + event);
		synchronized (lock) {
			_currentMmiEvent = event;
			lock.notifyAll();
		}
	}

	public MmiEvent getMmiEvent() {
		_log.debug("getMmiEvent ");
		synchronized (lock) {
			try {
				if (_currentMmiEvent == null) {
					_log.debug("wait ");
					lock.wait();
				}
			} catch (InterruptedException e) {
			}
			MmiEvent event = new MmiEvent(_currentMmiEvent);
			_currentMmiEvent = null;
			_log.debug("Mise a disposition de " + event);
			return event;
		}
	}

	public void refresh(MmiDialogInWrapper screen) {
		if (_mmi != null)
			_mmi.submit(screen.getDialogIn());
	}

	public void setMmi(Mmi mmi) {
		_mmi = mmi;
	}

	private class DialogOutWatcher implements Runnable {
		public void run() {
			while (_isOpened) {
				if (_mmi == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				} else {
					MmiDialogOut out = _mmi.getDialogOut();
					setMmiEvent(new MmiEvent(out.getAction(), out.getAppId(),
							out.getTextValue()));
				}
			}
		}

	}
}
