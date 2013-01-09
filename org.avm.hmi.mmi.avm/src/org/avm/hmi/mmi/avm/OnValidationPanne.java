package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnValidationPanne implements ProcessRunnable {

	private Avm _avm;
	private AvmView _avmView;
	private boolean _isV = true;

	public OnValidationPanne(Avm avm, boolean v, AvmView avmview) {
		_avm = avm;
		_avmView = avmview;
		_isV = v;
	}

	public void init(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run() {
		if (_isV) {
			_avmView.getBase().startAttente();
			_avm.panne();
		} else {
			_avmView.back();
		}

	}

}
