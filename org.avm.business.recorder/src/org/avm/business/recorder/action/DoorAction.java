package org.avm.business.recorder.action;

import org.avm.business.recorder.Action;
import org.avm.business.recorder.ActionFactory;
import org.avm.business.recorder.Journalizable;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.variable.Variable;

public class DoorAction extends Journalizable implements Action {

	public void compute(Object o) {
		Variable var = (Variable) o;
		if (var.getName().indexOf("porte") != -1) {
			StringBuffer buf = new StringBuffer();
			buf.append(var.getName().toUpperCase());
			buf.append(";");

			buf.append(var.getValue().getValue());
			buf.append(";");

			journalize(buf.toString());
		}
	}

	public void configure(Object o) {
		if (o instanceof JDB) {
			setJdb((JDB) o);
		}
	}

	static {
		ActionFactory.addAction(Variable.class, new DoorAction());
	}

}
