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
public class OnValidationFinSeg implements ProcessRunnable {

	private boolean _isV = true;
	private Avm _avm;
	private AvmView _avmView;

	public OnValidationFinSeg(Avm avm, boolean v, AvmView avmview) {
		_avm = avm;
		_avmView = avmview;
		_isV = v;
	}

	public void init(String[] args) {
	}

	public void run() {
		if (_isV) {
			_avmView.getBase().startAttente();
			_avm.finService();
		} else {
			_avmView.back();
		}

	}

}
