package org.avm.device.knet.mmi;

public interface Mmi {

	// public void setClient( MmiClient inMmiclient);
	public void submit(MmiDialogIn inMmiDialogIn);

	public MmiDialogOut getDialogOut();

}
