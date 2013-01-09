/**
 * 
 */
package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.business.core.AvmModel;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

/**
 * @author lbr
 * 
 */
public class OnValidationAllStates implements ProcessRunnable {

	private boolean _isV = true;
	private Avm _avm;
	private AvmView _avmView;
	private int _passwd;

	public OnValidationAllStates(Avm avm, boolean v, AvmView avmview) {
		_avm = avm;
		_avmView = avmview;
		_isV = v;
	}

	public void init(String[] args) {
		try {
			_passwd = new Integer(args[0]).intValue();
		} catch (NumberFormatException nfe) {
			System.out.println(">>>\tNumberFormatException : " + args[0]);
			_passwd = 0;
		}
		System.out.println(">>>\tinit passwd a " + _passwd);
	}

	public void run() {
		if (_isV) {
			if (_passwd != 4268) {
				_avmView.refresh();
			} else {
				_avmView.getBase().startAttente();
				_avmView.activateValidPanne("VALID_"
						+ AvmModel.STRING_STATE_EN_PANNE);
			}
		} else {
			_avm.annuler();
		}
		_passwd = -1;
	}

}
