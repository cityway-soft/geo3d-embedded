package org.avm.hmi.mmi.application.display;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.device.knet.mmi.Mmi;
import org.avm.device.knet.mmi.MmiDialogOut;
import org.avm.device.knet.mmi.MmiInjector;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;
import org.avm.hmi.mmi.application.actions.processFactory;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4Attente;

import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

public class AVMDisplayImpl implements AVMDisplay, MmiInjector {
	private List _drawingOrder = Collections.synchronizedList(new ArrayList());
	private processFactory _processes = processFactory.createInstance();
	private Thread _thread;
	private boolean _scanning;
	private Logger _log;
	private Mmi _mmi;
	private MmiDialogInWrapper _fgScreen;
	private MmiDialogInWrapper4Attente _screenAttente = MmiDialogInWrapper4Attente
			.getNewInstance("PROGRESSBAR");
	private ThreadedExecutor _executor;
	public boolean _forceStopAttente;
	private Thread _progressor = null;

	public void start() {
		_log = Logger.getInstance(this.getClass().getName());
		_executor = new ThreadedExecutor();
		// _log.setPriority(Priority.DEBUG);
	}

	public void stop() {
		_log.debug("stopping avmdisplay...");
		_log.debug("avmdisplay stopped (display=null).");
	}

	public void setMmi(Mmi mmi) {
		_log.debug("SetMmi");
		_mmi = mmi;
		setProcess("null", "null", new OnNull());
		if (mmi != null)
			open();
		else
			close();
	}

	public void open() {
		_log.debug("opening avmdisplay...");
		_scanning = true;
		_thread = new Thread(new ScanDialogOut());
		_thread.start();
		submit();
		_log.debug("avmdisplay opened");
	}

	public void close() {
		_log.debug("closing avmdisplay...");
		_scanning = false;
		_thread = null;
		_log.debug("avmdisplay closed.");
	}

	public void setProcess(String action, String state, ProcessRunnable process) {
		_processes.addProcess(action, state, process);
	}

	public void setProcessArgs(String action, String etat, String[] args) {
		_processes.setArgs(action, etat, args);
	}

	public ProcessRunnable getProcess(String action, String etat) {
		return _processes.getProcess(action, etat);
	}

	public void setScreen2fg(MmiDialogInWrapper screen) {
		_log.debug("setScreen2fg " + screen);
		synchronized (_drawingOrder) {
			int i = _drawingOrder.indexOf(screen);
			if (i != 0) {
				if (i > 0) {
					screen = (MmiDialogInWrapper) _drawingOrder.remove(i);
				}
				_drawingOrder.add(0, screen);
			}
		}
		screen.resetDialogIn();
		submit();
	}

	public void updateScreen2fg(MmiDialogInWrapper screen) {
		// _log.debug("updateScreen2fg "+screen);
		synchronized (_drawingOrder) {
			int i = _drawingOrder.indexOf(screen);
			if (i != 0) {
				if (i > 0) {
					screen = (MmiDialogInWrapper) _drawingOrder.remove(i);
				}
				_drawingOrder.add(0, screen);
			}
		}
		submit();
	}

	public MmiDialogInWrapper getFgScreen() {
		return (MmiDialogInWrapper) _drawingOrder.get(0);
	}

	public void back() {
		_log.debug("back ...");
		synchronized (_drawingOrder) {
			MmiDialogInWrapper s = (MmiDialogInWrapper) _drawingOrder.remove(0);
			_drawingOrder.add(s); // Le met à la fin
			((MmiDialogInWrapper) _drawingOrder.get(0)).resetDialogIn();
		}
		submit();
	}

	public void setView2fgAlways(MmiDialogInWrapper screen) {
		_fgScreen = screen;
		submit();
	}

	public void unsetViewFromfgAlways(String key, MmiDialogInWrapper screen) {
		_log.debug("unsetViewFromfgAlways " + screen);
		if (screen.equals(_fgScreen))
			_fgScreen = null;

		synchronized (_drawingOrder) {
			int i = _drawingOrder.indexOf(screen);
			_log.debug("couche " + i);
			if (i == 0) {
				MmiDialogInWrapper s = (MmiDialogInWrapper) _drawingOrder
						.remove(i);
				_drawingOrder.add(s); // Le met à la fin
			}
			MmiDialogInWrapper mmi = (MmiDialogInWrapper) _drawingOrder.get(0);
			mmi.resetDialogIn();
			mmi.resetSoftKey(key);
			mmi.clearMessages();
		}
		submit();
	}

	public void submit() {
		if (_mmi == null)// dans le cas ou ce bundles est instancié avant mmi
			return;
		stopAttente();
		if (_fgScreen != null) {
			_log.debug("submitting _fgScreen " + _fgScreen);
			_mmi.submit(_fgScreen.getDialogIn());
			return;
		}
		synchronized (_drawingOrder) {
			MmiDialogInWrapper dw = (MmiDialogInWrapper) _drawingOrder.get(0);
			_log.debug("submitting dw " + dw);
			if (dw != null)
				_mmi.submit(dw.getDialogIn());
			else {
				_log.debug(" _drawingOrder.get(0) est null");
				startAttente();
			}
		}
	}

	public void resetSoftKey2fgView(String key) {
		synchronized (_drawingOrder) {
			MmiDialogInWrapper mmi = (MmiDialogInWrapper) _drawingOrder.get(0);
			mmi.resetSoftKey(key);
			_mmi.submit(mmi.getDialogIn());
		}
	}

	public void setSoftKey2fgView(String key, String sk) {
		synchronized (_drawingOrder) {
			MmiDialogInWrapper mmi = (MmiDialogInWrapper) _drawingOrder.get(0);
			mmi.setSoftKey(key, sk);
			_mmi.submit(mmi.getDialogIn());
		}
	}

	public void setMessage(String msg) {
		Calendar calNow = GregorianCalendar.getInstance();
		int HHnow = calNow.get(Calendar.HOUR_OF_DAY);
		int MMnow = calNow.get(Calendar.MINUTE);
		StringBuffer sb = new StringBuffer(5);
		sb.append("[");
		sb.append(HHnow);
		sb.append(":");
		sb.append(MMnow);
		sb.append("]-");
		sb.append(msg);
		synchronized (_drawingOrder) {
			((MmiDialogInWrapper) _drawingOrder.get(0)).setMessage(sb
					.toString());
		}
		submit();
	}

	public void clearMessages() {
		synchronized (_drawingOrder) {
			((MmiDialogInWrapper) _drawingOrder.get(0)).clearMessages();
		}
	}

	private class OnNull implements ProcessRunnable {

		public void init(String[] args) {
		}

		public void run() {
			clearMessages();
			submit();
		}

	}

	private class ScanDialogOut implements Runnable {
		public void run() {
			while (_scanning) {
				MmiDialogOut out = _mmi.getDialogOut(); // Appel bloquant
				try {
					if (out != null) {
						if ((out.getTextValue() != null)
								&& (out.getTextValue().length() > 0)) {
							String[] args = { out.getTextValue() };
							_processes.setArgs(out.getAction(), out.getAppId(),
									args);
						}
						_processes.launchProcess(out.getAction(), out
								.getAppId());
					}
				} catch (Throwable t) {
					_log.error("Scan dialogOut loop error", t);
				}
			}
		}

	}

	public void startAttente() {
		_forceStopAttente = false;
		_screenAttente.resetDialogIn();
		if (_progressor == null) {// Sinon c'est déjà en cours ...
			_progressor = _executor.getThreadFactory().newThread(
					new Progressor());
			_mmi.submit(_screenAttente.getDialogIn());
			_progressor.start();
		}
	}

	public void stopAttente() {
		_forceStopAttente = true;
		if (_progressor != null)
			_progressor.interrupt();
		_progressor = null;
		_screenAttente.resetProgress();
	}

	private class Progressor implements Runnable {
		int _drawingOrdersize = 0;

		public Progressor() {
		}

		private boolean isAttenteFinished() {
			boolean b = false;
			synchronized (_drawingOrder) {
				b = _drawingOrdersize != _drawingOrder.size();
			}
			return (b || _forceStopAttente);
		}

		public void run() {
			System.out.println(">>>startProgress::run()...");
			synchronized (_drawingOrder) {
				_drawingOrdersize = _drawingOrder.size();
			}
			while (isAttenteFinished() == false) {
				_screenAttente.progress();
				_mmi.submit(_screenAttente.getDialogIn());

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out
							.println(">>>Interruption du Thread d'attente ...");
				}
			}
			System.out.println(">>>startProgress::run()...EXIT");
		}
	}

}
