package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.business.core.AvmModel;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnSelectionFinServ implements ProcessRunnable {

	private AvmView _avmView;
	private Avm _avm;

	public OnSelectionFinServ(Avm avm, AvmView view) {
		_avmView = view;
		_avm = avm;
	}

	public void init(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run() {
		_avmView.activateValidFinService("VALID_FIN_"
				+ AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE, _avm
				.getModel().getCourse().toString());
	}

}
