package org.avm.hmi.mmi.phony;

import org.avm.device.phony.Phony;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;

public class OnSelectionHangUp implements ProcessRunnable {

	private PhonyControler _controler;
	private Phony _phony;

	public OnSelectionHangUp(PhonyControler controler, Phony phony) {
		_controler = controler;
		_phony = phony;
	}

	public void init(String[] args) {
	}

	public void run() {
		try {
			if (_controler.isOnline()) {
				// si on est deccroché, on raccroche
				_phony.hangup();
			} else {
				// si on est raccroché, on decroche
				_phony.answer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPhony(Phony phony) {
		_phony = phony;
	}

}
