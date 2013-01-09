package org.avm.hmi.mmi.phony;

import org.avm.business.ecall.EcallService;
import org.avm.business.ecall.EcallServiceImpl;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;
import org.avm.hmi.mmi.application.screens.MmiConstantes;
import org.osgi.util.measurement.State;

public class OnSelectionCM implements ProcessRunnable {

	private PhonyView _phonyView;
	private EcallService _eCall;

	public OnSelectionCM(PhonyView view, EcallService call) {
		_phonyView = view;
		_eCall = call;
	}

	public void init(String[] args) {
	}

	public void setEcall(EcallService ecall) {
		_eCall = ecall;
	}

	public void setView(PhonyView view) {
		_phonyView = view;
	}

	public void run() {
		boolean success = false;
		if (_eCall == null) {
			System.out.println("ECALL n'est pas initialise !");
			_phonyView.refresh();
			return;
		}
		State st = _eCall.getState();
		if (!EcallServiceImpl.STATE_NO_ALERT.equals(st.getName())) {
			_eCall.endEcall();
		}
		success = _eCall.startEcall();
		if (success) {
			_phonyView.changeSoftKey(MmiConstantes.F1,
					MmiConstantes.SK_APP_EN_COURS);
		} else {
			_eCall.endEcall();
		}
	}

}
