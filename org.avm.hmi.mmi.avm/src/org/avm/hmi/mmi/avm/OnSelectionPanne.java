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
public class OnSelectionPanne implements ProcessRunnable {

	private AvmView _avmView;
	private AvmControler _controler;

	public OnSelectionPanne(AvmView view, AvmControler controler) {
		_avmView = view;
		_controler = controler;
	}

	public void init(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run() {
		_controler.stopAttenteDepart();
		_avmView.activateValidPanne("VALID_" + AvmModel.STRING_STATE_EN_PANNE);
	}

}
