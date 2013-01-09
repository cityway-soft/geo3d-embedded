package org.avm.hmi.mmi.phony;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.business.ecall.EcallService;
import org.avm.business.ecall.EcallServiceImpl;
import org.avm.business.protocol.phoebus.ClotureAlerte;
import org.avm.business.protocol.phoebus.PriseEnCharge;
import org.avm.device.phony.PhoneEvent;
import org.avm.device.phony.PhoneRingEvent;
import org.avm.device.phony.Phony;
import org.avm.elementary.variable.Variable;
import org.avm.hmi.mmi.application.actions.Keys;
import org.avm.hmi.mmi.application.actions.ProcessCustomizer;
import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.application.screens.MmiConstantes;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.State;

public class PhonyControler implements ProcessCustomizer, MmiPhony {
	private static final String ETAT_CALL = "entre ring et raccrocher";

	private PhonyView _phonyView;

	private AVMDisplay _base;

	private Logger _log;

	private boolean _onLine;

	private EcallService _eCall;

	private Phony _phony;

	private Variable _beeper;
	private OnSelectionCM _onSelectionCM;
	private OnSelectionHangUp _onSelectionHangUp;

	public PhonyControler() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_onSelectionCM = new OnSelectionCM(_phonyView, _eCall);
		_onSelectionHangUp = new OnSelectionHangUp(this, _phony);
	}

	public void start() {
		_phonyView = PhonyView.createInstance(_base);
		_onSelectionCM.setView(_phonyView);
		addProcess();
		_onLine = false;
	}

	public void stop() {
	}

	public void addProcess() {
		_base.setProcess(Keys.KEY_CM, "ALL_STATES", _onSelectionCM);
		_base.setProcess(Keys.KEY_HANG_UP, "ALL_STATES", _onSelectionHangUp);
	}

	public void setBase(AVMDisplay base) {
		_base = base;
	}

	public void setEcall(EcallService ecall) {
		_eCall = ecall;
		_onSelectionCM.setEcall(ecall);
	}

	public void setPhony(Phony phony) {
		_phony = phony;
		_onSelectionHangUp.setPhony(phony);
	}

	public void setBeeper(Variable var) {
		_beeper = var;
	}

	public boolean isOnline() {
		return _onLine;
	}

	public void notify(Object o) {
		if (o instanceof PhoneEvent) {
			_log.debug("Receive 'PhoneEvent' : " + o); //$NON-NLS-1$
			onPhoneEvent((PhoneEvent) o);
		} else if (o instanceof PriseEnCharge) {
			_log.debug("Receive 'PriseEnCharge' : " + o); //$NON-NLS-1$
			onPriseEnCharge((PriseEnCharge) o);
		} else if (o instanceof ClotureAlerte) {
			_log.debug("Receive 'ClotureAlerte' : " + o); //$NON-NLS-1$
			onClotureAlerte((ClotureAlerte) o);
		}
	}

	private void onClotureAlerte(ClotureAlerte alerte) {
		_base.setMessage(Messages.getString("clotureAlerte"));
		resetCM();
	}

	private void onPriseEnCharge(PriseEnCharge pec) {
		_log.debug("onPriseEnCharge(" + pec + ")");
		_base.setMessage(Messages.getString("priseEnCharge"));
		resetCM();
	}

	private void resetCM() {
		if (_eCall == null) {
			_base.setMessage(Messages.getString("ErreurCM"));
			return;
		}
		State st = _eCall.getState();
		if (!EcallServiceImpl.STATE_NO_ALERT.equals(st.getName())) {
			_eCall.endEcall();
		}
		_phonyView.resetSoftKey(MmiConstantes.F1);
	}

	private void onPhoneEvent(PhoneEvent event) {
		if (event.getStatus() == PhoneEvent.RING) {
			if (_beeper != null)
				_beeper.setValue(new Measurement(1));
			String phoneNumber = ((PhoneRingEvent) event).getCallingNumber();
			_log.debug("Ca sonne : " + phoneNumber); //$NON-NLS-1$
			// Pour contourner un bug du parsing XML de l'agent Knet
			if (!phoneNumber.startsWith("0"))
				phoneNumber = Messages.getString("phony.hidden");
			_phonyView.drawRinging(ETAT_CALL, phoneNumber);
			return;
		}
		if (event.getStatus() == PhoneEvent.ON_LINE) {
			_log.debug("on cause"); //$NON-NLS-1$
			_onLine = true;
			_phonyView.drawCalling();
			return;
		}
		if (event.getStatus() == PhoneEvent.READY) {
			_log.debug("On a raccroche ; pret pour un nouvel appel"); //$NON-NLS-1$
		}
		// Pour les autres evenements :
		if (event.getStatus() == PhoneEvent.BUSY) {
			_log.debug(Messages.getString("phony.busy")); //$NON-NLS-1$
			_base.setMessage(Messages.getString("phony.busy")); //$NON-NLS-1$
		}
		if (event.getStatus() == PhoneEvent.NO_CARRIER) {
			_log.debug(Messages.getString("phony.nocarrier")); //$NON-NLS-1$
			_base.setMessage(Messages.getString("phony.nocarrier")); //$NON-NLS-1$
		}
		if (event.getStatus() == PhoneEvent.DIALING) {
			_log.debug("On compose un numero"); //$NON-NLS-1$
			_base.clearMessages(); //$NON-NLS-1$
		}
		if (event.getStatus() == PhoneEvent.ERROR) {
			_log.debug(Messages.getString("phony.error")); //$NON-NLS-1$
			_base.setMessage(Messages.getString("phony.error")); //$NON-NLS-1$
		}
		// _phonyView.resetSoftKey(MmiConstantes.F1);
		_phonyView.unsetPhoneScreenFgAndReset(MmiConstantes.F1);
		_onLine = false;
	}

}
