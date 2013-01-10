package org.avm.hmi.swt.phony;

import org.eclipse.swt.events.SelectionListener;

public interface CallAnswerSelector {
	public static int ANSWER_MODE=0;
	public static int CALL_MODE=1;
	public void setMode(int mode, SelectionListener listener);

}
