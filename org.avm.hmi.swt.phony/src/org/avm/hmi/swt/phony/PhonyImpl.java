package org.avm.hmi.swt.phony;

import org.apache.log4j.Logger;
import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmInjector;
import org.avm.device.phony.PhoneEvent;
import org.avm.device.phony.PhoneRingEvent;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.directory.Directory;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopInjector;
import org.avm.hmi.swt.desktop.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.util.measurement.State;

public class PhonyImpl implements Phony, ConsumerService, ManageableService,
		GsmInjector, UserSessionServiceInjector, DesktopInjector, JDBInjector {
	protected static final String NAME = Messages.getString("PhonyImpl.Phonie"); //$NON-NLS-1$
	public static final String JDB_TAG = "swt.phony";

	private PhonyIhm _phonyihm;

	private SignalLevelIhm _signalLevel;

	private org.avm.device.phony.Phony _phone;

	private static final int PERIOD = 10000;

	private Desktop _desktop;

	private Logger _log;

	private Display _display;

	private Directory _directory;

	private QualitySignalTask _task;

	private Scheduler _scheduler;

	private Gsm _gsm;

	private UserSessionService _session;

	private PhonyImpl _instance;

	private ContactModel _model;

	private Object _taskId;

	private JDB _jdb;

	public PhonyImpl() {
		_log = Logger.getInstance(this.getClass());
		_instance = this;
		_model = new ContactModel();
		_scheduler = new Scheduler();
		//_log.setPriority(Priority.DEBUG);
	}

	public void start() {

	}

	public void stop() {
		closePhony();
		closeSignalLevel();
	}

	private void openPhony() {
		System.out.println("Opening phony....");
		if (_phonyihm == null) {
			_display.syncExec(new Runnable() {
				public void run() {
					_phonyihm = new PhonyIhm(_desktop.getMainPanel(),
							SWT.NONE);
					_phonyihm.setPhony(_instance);
					_phonyihm.update(_model);
					checkEnable();
					setPhony(_phone); 
					_desktop.addTabItem(NAME, _phonyihm);
					if (System
							.getProperty("org.avm.mode", "siv").equals("securite")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						_desktop.activateItem(NAME);
					}
				}

			});
		}
	}

	private void closePhony() {
		if (_phonyihm != null) {
			_display.syncExec(new Runnable() {
				public void run() {
					_phonyihm.dispose();
					if (_desktop != null) {
						_desktop.removeTabItem(NAME);
					}
					_phonyihm = null;
				}
			});
		}
	}

	private void checkPhony() {
		if (_display != null){
			_log.debug("model size="+_model.size());
			
			if (_model.size() != 0 || _session.hasRole("admin")){
				openPhony();
			}
			else{
				closePhony();
			}
		}
	}

	private void openSignalLevel() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (_signalLevel == null) {
					_signalLevel = new SignalLevelIhm(_desktop.getRightPanel(),
							SWT.BORDER);
					checkEnable();
				}
			}
		});
	}

	private void closeSignalLevel() {
		if (_taskId != null) {
			_scheduler.cancel(_taskId);
			_taskId = null;
		}
		_display.syncExec(new Runnable() {
			public void run() {
				if (_signalLevel != null) {
					_signalLevel.dispose();
					_signalLevel = null;
				}
			}
		});
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(UserSessionService.class.getName())) {
					checkPhony();
					checkLogin();
			}
		} else if (o instanceof PhoneEvent) {
			if (_phonyihm == null || _phonyihm.isDisposed()) {
				return;
			}
			PhoneEvent msg = (PhoneEvent) o;
			_log.debug("Phony Event : " + o); //$NON-NLS-1$
			switch (msg.getStatus()) {
			case PhoneEvent.DIALING: {
				_log.debug("Phony Event Dialing"); //$NON-NLS-1$
				_phonyihm.dialing();
			}
				break;
			case PhoneEvent.RING: {
				_log.debug("Phony Event Ring"); //$NON-NLS-1$
				_desktop.activateItem(NAME);
				PhoneRingEvent re = (PhoneRingEvent) msg;
				_desktop.activateItem(NAME);
				_phonyihm.ringing(re.getCallingNumber());
				setVolume(_phonyihm.getVolume());
			}
				break;
			case PhoneEvent.CONTACT_LIST_CHANGED: {
				_log.debug("Phony Event Contact List"); //$NON-NLS-1$
				_phonyihm.update(_model);
			}
				break;
			case PhoneEvent.ON_LINE: {
				_log.debug("Phony Event OnLine"); //$NON-NLS-1$
				_phonyihm.online();
			}
				break;

			case PhoneEvent.READY: {
				_phonyihm.setEnabled(true);
			}

			case PhoneEvent.NO_CARRIER:
			case PhoneEvent.ERROR: {
				_log.debug("Phony Event Ready/Error/No Carrier"); //$NON-NLS-1$
				_phonyihm.hangup();
			}
				break;
			case PhoneEvent.MODEM_NOT_AVAILABLE: {
				_phonyihm.setEnabled(false);
			}
				break;
			}
		}

	}

	public void checkEnable() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (_signalLevel != null && _signalLevel.isDisposed() == false) {
					_signalLevel.setEnabled((_gsm != null));
				}
			}
		});
		
		if (_gsm == null){
			if (_taskId != null) {
				_log.debug("un-schedule task !");
				_scheduler.cancel(_taskId);
				_taskId = null;
			}
		}
		else{
			if (_taskId == null) {
				_log.debug("schedule task !");
				_taskId = _scheduler.schedule(_task, PERIOD, true);
			}
		}

		if (_phonyihm != null) {
			_phonyihm.setEnabled((_gsm != null) && (_phone != null));
		}
	}

	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
		if (_desktop != null) {
			_display = _desktop.getDisplay();
		}
		_task = new QualitySignalTask();
		checkPhony();
		openSignalLevel();
	}
	
	public void unsetDesktop(Desktop desktop) {
	
	}

	// gsm
	public void setGsm(Gsm gsm) {
		_gsm = gsm;
		checkEnable();
	}

	public void unsetGsm(final Gsm gsm) {
		_gsm = null;
		checkEnable();
	}

	public void activateKeyboard(boolean activate) {
		if (_phonyihm != null) {
			_phonyihm.activateKeyboard(activate);
		}
	}

	// Phony
	public void setPhony(org.avm.device.phony.Phony phone) {
		_phone = phone;
		if (_phone != null && _phonyihm != null) {
			_phonyihm.update(_model);
			_phonyihm.setVolume(_phone.getDefaultSoundVolume());
			setVolume(_phone.getDefaultSoundVolume());
		}
	}

	public void unsetPhony(org.avm.device.phony.Phony phony) {
		_phone = null;
		if (_phonyihm != null) {
			_phonyihm.setPhony(null);
		}
	}

	// Directory
	public void setDirectory(Directory service) {
		_directory = service;
		_model.update(_directory);
		checkPhony();
		if (_phonyihm != null) {
			_phonyihm.update(_model);
		}
	}

	public void unsetDirectory(Directory service) {
		_directory = service;
		_model.update(null);
		if (_phonyihm != null) {
			_phonyihm.update(_model);
		}
	}

	// Usersession
	public void setUserSessionService(UserSessionService service) {
		_session = service;
		notify(_session.getState());
	}

	public void unsetUserSessionService(UserSessionService service) {
		_session = service;
		closePhony();
	}

	class QualitySignalTask implements Runnable {
		private int _previousAttachment;

		public void run() {
			if (_gsm != null) {
				try {
					if (_log.isDebugEnabled()) {
						_log.debug("Check GSM Quality Signal...:");
					}
					int quality = _gsm.getSignalQuality();
					if (_taskId == null)
						return;
					_signalLevel.setSignalQuality(quality);

					// gsm attached ?
					int attached = _gsm.isGsmAttached() ? ATTACHEMENT_GSM_OK
							: ATTACHEMENT_NONE;

					// gprs attached ?
					if (attached == ATTACHEMENT_GSM_OK) {
						attached = _gsm.isGprsAttached() ? ATTACHEMENT_GSM_GPRS_OK
								: ATTACHEMENT_GSM_OK;
					}
					if (attached != _previousAttachment){
						 jounalizeState(attached, quality);
						_previousAttachment = attached;
					}
					
					if (_taskId == null)
						return;
					_signalLevel.setAttachment(attached);

					if (_log.isDebugEnabled()) {
						_log.debug("Modem Attachement State : " + attached
								+ "; level :" + quality);
					}
				} catch (Throwable t) {
					_log.error("Error QualitySignalTask : " + t.getMessage());
				}
			}
		}

	}

	public void checkLogin(){
		boolean logged=false;
		boolean activateKeyboad=false;
		if (_session != null) {
			logged = (_session.getState().getValue()==UserSessionService.AUTHENTICATED);
			activateKeyboad = _session.hasRole("phone") && logged;
		}
		activateKeyboard(activateKeyboad);		
	}

	public void call(String name) throws Exception {
		String phoneNumber = _model.getPhoneNumber(name);
		if (phoneNumber == null) {
			throw new Exception("No phone number for '" + name + "'");
		}
		_log.info("call " + name);
		setVolume(_phonyihm.getVolume());
		_phone.dial(phoneNumber);
		_log.info("dialing in progress (call " + name + ")...");
	}

	public void dial(String number) {
		if (_phone != null) {
			try {
				setVolume(_phonyihm.getVolume());
				_phone.dial(number);
				_log.info("dialing in progress...");
			} catch (Exception e) {
				MessageBox.setMessage("Erreur",
						"Impossible de composer le numéro (" + e.getMessage()
								+ ")", MessageBox.MESSAGE_ALARM, SWT.NONE);
				_log.error("GsmException", e); //$NON-NLS-1$
			}
		} else {
			_log.error("no phone available."); //$NON-NLS-1$
		}
	}

	public void answer() {
		if (_phone != null) {
			try {
				_phone.answer();
			} catch (Exception e) {
				_log.error(e);
			}
		} else {
			_log.error("no phone available."); //$NON-NLS-1$
		}
	}

	public void hangup() {
		if (_phone != null) {
			try {
				_phone.hangup();
				if (_phonyihm != null) {
					setVolume(_phonyihm.getVolume());
				}
			} catch (Exception e) {
				_log.error(e);
			}
		} else {
			_log.error("no phone available."); //$NON-NLS-1$
		}
	}

	public void setVolume(int value) {
		_phone.setVolume(value);
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	public void jounalizeState(int attached, int quality){
		StringBuffer log=new StringBuffer();
		log.append("ATTACH;");
		log.append(quality);
		switch (attached) {
		case Phony.ATTACHEMENT_NONE:
			log.append(";none");
			break;
		case Phony.ATTACHEMENT_GSM_GPRS_OK:
			log.append(";gprs");
			break;
		case Phony.ATTACHEMENT_GSM_OK:
			log.append(";gsm");
			log.append(quality);
			break;
		default:
			break;
		}
		journalize(log.toString());
	}
	
	public void journalize(String message) {
		_log.debug(message);
		if (_jdb != null) {
			try {
				_jdb.journalize(JDB_TAG, message);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

}