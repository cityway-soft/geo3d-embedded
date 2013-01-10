package org.avm.hmi.mmi.avm;

import org.avm.business.core.AvmModel;
import org.avm.business.core.event.ServiceAgent;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnSelectionServiceSpec implements ProcessRunnable {

	private AvmView _avmView;
	private int _typeServSpec;

	public OnSelectionServiceSpec(AvmView view, int typeServ) {
		_avmView = view;
		_typeServSpec = typeServ;
	}

	public void init(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run() {
		if (ServiceAgent.SERVICE_OCCASIONNEL == _typeServSpec)
			_avmView.activateValidChoixSO("VALID_SO_"
					+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL);
		if (ServiceAgent.SERVICE_KM_A_VIDE == _typeServSpec)
			_avmView.activateValidChoixKM("VALID_KM_"
					+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL);

	}

}
