package org.avm.business.ecall;

public interface EcallServiceStateMachine {

	public void entryNoAlert();

	public void entryWaitAck();

	public void entryListenMode();

	public void call(String phone);

}
