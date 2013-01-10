package org.avm.device.generic.phony.impl;


public interface PhonyServiceStateMachine {

	boolean isGsmAvailable();
	
	void initialize();
	
	void ringing(String phone);
	
	void answering();
	
	void dialing(String phone, boolean listen);
	
	void entryClosed();
	
	void entryReady();
	
	void exitRinging();

	void entryDialing();
	
	void exitDialing();

	void entryOnline();

}
