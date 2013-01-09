package org.avm.hmi.mmi.avm;

import org.avm.business.core.AvmModel;
import org.avm.business.core.event.ServiceAgent;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnSelectionFinServSpec implements ProcessRunnable {

	private AvmView _avmView;
	private int _typeService;

	public OnSelectionFinServSpec(AvmView view, int typeServ) {
		_avmView = view;
		_typeService = typeServ;
	}

	public void init(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run() {
		if (ServiceAgent.SERVICE_OCCASIONNEL == _typeService)
			_avmView.activateValidFinSo("VALID_FIN_SO_"
					+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL);

		if (ServiceAgent.SERVICE_KM_A_VIDE == _typeService)
			_avmView.activateValidFinKm("VALID_FIN_KM_"
					+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL);
	}

}
