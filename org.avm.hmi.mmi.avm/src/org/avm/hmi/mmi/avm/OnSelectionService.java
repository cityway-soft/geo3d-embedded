/**
 * 
 */
package org.avm.hmi.mmi.avm;

import org.avm.business.core.AvmModel;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

/**
 * @author lbr
 * 
 */
public class OnSelectionService implements ProcessRunnable {

	private AvmView _avmView;

	public OnSelectionService(AvmView view) {
		_avmView = view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.elementary.ihm.mmi.application.actions.ProcessRunnable#init(java.lang.String[])
	 */
	public void init(String[] args) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		_avmView
				.activatePriseDeService(AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE);
	}

}
