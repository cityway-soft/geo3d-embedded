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
public class OnValidationNoSeg implements ProcessRunnable {

	private Avm _avm;

	private AvmView _avmView;

	private int _numSegment;

	private boolean _isV = true;

	public OnValidationNoSeg(Avm avm, boolean v, AvmView avmview) {
		_avm = avm;
		_avmView = avmview;
		_isV = v;
	}

	public void init(String[] args) {
		try {
			_numSegment = new Integer(args[0]).intValue();
		} catch (NumberFormatException nfe) {
			System.out.println(">>>\tNumberFormatException : " + args[0]);
			_numSegment = -1;
		}
	}

	public void run() {
		if (_isV && _numSegment > 0) {
			_avmView.getBase().startAttente();
			_avm.priseService(_numSegment);
		} else {
			_avm.annuler();
			_avmView
					.activateSaisieService(AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE);
		}
	}

}
