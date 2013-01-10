package org.avm.hmi.mmi.avm;

import org.avm.business.core.Avm;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnValidationServSpec implements ProcessRunnable {

	private boolean _isV = true;
	private Avm _avm;
	private AvmView _avmView;
	private int _typeServSpec;

	public OnValidationServSpec(Avm avm, boolean v, int typeServ, AvmView view) {
		_avmView = view;
		_avm = avm;
		_isV = v;
		_typeServSpec = typeServ;
	}

	public void init(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run() {
		if (_isV) {
			_avmView.getBase().startAttente();
			_avm.priseService(_typeServSpec);
		} else {
			_avmView.back();
		}
	}

}
