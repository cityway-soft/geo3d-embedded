package org.avm.business.recorder.action;

import org.avm.business.recorder.Action;
import org.avm.business.recorder.ActionFactory;
import org.avm.business.recorder.Journalizable;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.position.Position;

public class PositionAction extends Journalizable implements Action {

	private int _capReference = 0;

	private int _dernierCap = 0;

	private boolean _variationCap;

	public void compute(Object p) {
		Position position = (Position) p;
		if (position.getSpeed().getValue() > 1d) {
			int cap = (int) (position.getTrack().getValue() * 180d / Math.PI);
			int delta = Math.abs((cap - _dernierCap));
			if (delta <= 1) {
				delta = Math.abs((cap - _capReference));
				if (delta > 4 && _variationCap == true) {
					_variationCap = false;
					_capReference = cap;
					journalize("AUTOd"); //$NON-NLS-1$
				}
			} else {
				delta = Math.abs((cap - _capReference));
				if (delta > 2 && _variationCap == false) {
					_capReference = cap;
					journalize("AUTOv"); //$NON-NLS-1$
				}
				_variationCap = true;
			}
			_dernierCap = cap;
		}
	}

	public void configure(Object o) {
		if (o instanceof JDB) {
			setJdb((JDB) o);
		}
	}

	static {
		ActionFactory.addAction(Position.class, new PositionAction());
	}

}
