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
public class OnValidationGeolocaliseArret implements ProcessRunnable {

	private Avm _avm;
	private boolean _isValid = false;
	private AvmView _avmView;;

	public OnValidationGeolocaliseArret(Avm avm, AvmView view, boolean devie) {
		_isValid = devie;
		_avmView = view;
		_avm = avm;
	}

	public void init(String[] args) {
	}

	public void run() {
		if (_isValid) {
			// l'arret ainsi geolocalisé doit pouvoir ensuite être repéré
			// dans le journal de bord.
			_avmView.getBase().startAttente();
			_avm.entree(_avm.getModel().getProchainPoint().getId());
		} else {
			_avmView.back();
		}
	}

}
