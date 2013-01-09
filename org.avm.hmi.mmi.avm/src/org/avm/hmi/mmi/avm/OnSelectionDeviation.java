/**
 * 
 */
package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

/**
 * @author lbr
 * 
 */
public class OnSelectionDeviation implements ProcessRunnable {

	private AvmView _avmView;
	private Avm _avm;

	public OnSelectionDeviation(Avm avm, AvmView view) {
		_avm = avm;
		_avmView = view;
	}

	public void init(String[] args) {
	}

	public void run() {
		_avmView.getBase().startAttente();
		_avm.sortieItineraire();
	}

}
