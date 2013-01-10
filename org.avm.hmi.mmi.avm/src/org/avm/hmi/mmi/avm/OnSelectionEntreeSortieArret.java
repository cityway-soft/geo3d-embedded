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
public class OnSelectionEntreeSortieArret implements ProcessRunnable {

	private AvmView _avmView;
	private Avm _avm;
	private boolean _in = true;
	private static int _currentBalise = -1;

	public OnSelectionEntreeSortieArret(Avm avm, AvmView view, boolean inout) {
		_in = inout;
		_avm = avm;
		_avmView = view;
	}

	public void init(String[] args) {
	}

	public void run() {
		if (_in) {
			_avmView.getBase().startAttente();
			_currentBalise = _avm.getModel().getProchainPoint().getId();
			_avmView.setArretManuel();
			_avm.entree(_currentBalise);
		} else {
			_avmView.getBase().startAttente();
			_avmView.resetArretManuel();
			_avm.sortie(_currentBalise);
			_currentBalise = -1;
			// _avmView.activateValidGeoloc(AvmModel.STRING_STATE_EN_COURSE_HORS_ITINERAIRE,
			// _avm.getModel().getProchainPoint().getNom());
		}
	}

}
