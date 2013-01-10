package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnSelectionFinPanne implements ProcessRunnable {

	private Avm _avm;

	public OnSelectionFinPanne(Avm avm) {
		_avm = avm;
	}

	public void init(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run() {
		_avm.finPanne();
		// _avmView.activateValidFinPanne("VALID_FIN_"+AvmModel.STRING_STATE_EN_PANNE);
	}

}
