package org.avm.business.recorder.action;

import org.avm.business.recorder.Action;
import org.avm.business.recorder.ActionFactory;
import org.avm.business.recorder.Journalizable;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.position.Position;

public class MotionAction extends Journalizable implements Action {

	private long _t0 = -1;

	public void compute(Object o) {

		Position position = (Position) o;
		double speed = position.getSpeed().getValue();

		if (speed < 0.2) {
			if (_t0 == -1) {
				_t0 = System.currentTimeMillis();
			}
		} else if (_t0 != -1) {
			long data = (System.currentTimeMillis() - _t0) / 1000;
			if (data > 30) {
				journalize("IMMO;" + Long.toString(data));
			}
			_t0 = -1;
		}
	}

	public void configure(Object o) {
		if (o instanceof JDB) {
			setJdb((JDB) o);
		}
	}

	static {
		ActionFactory.addAction(Position.class, new MotionAction());
	}

}
