package org.avm.hmi.mmi.application.display;

import org.avm.hmi.mmi.application.actions.ProcessRunnable;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper;

public interface AVMDisplay {
	public void setScreen2fg(MmiDialogInWrapper s);

	// public MmiEvent getMmiEvents();
	public void submit();

	public void setMessage(String msg);

	public void setProcess(String action, String etat, ProcessRunnable process);

	public ProcessRunnable getProcess(String action, String etat);

	public void setProcessArgs(String action, String etat, String[] args);

	public void clearMessages();

	public void setSoftKey2fgView(String key, String sk);

	public void resetSoftKey2fgView(String key);

	// public void unsetViewFromfg(String key, MmiDialogInWrapper screen);
	public void unsetViewFromfgAlways(String key, MmiDialogInWrapper screen);

	public void setView2fgAlways(MmiDialogInWrapper screen);

	public void back();

	public void updateScreen2fg(MmiDialogInWrapper screen);

	public MmiDialogInWrapper getFgScreen();

	public void startAttente();

	public void stopAttente();
}
