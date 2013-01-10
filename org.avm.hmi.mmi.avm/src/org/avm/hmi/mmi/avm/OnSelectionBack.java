package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnSelectionBack implements ProcessRunnable {

	private Avm _avm = null;

	// private String _etat = null;

	public OnSelectionBack(Avm avm) {
		_avm = avm;
		// _etat = etat;
	}

	public void init(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run() {
		// On souhaite revenir au menu precedent
		_avm.annuler();
	}

}
