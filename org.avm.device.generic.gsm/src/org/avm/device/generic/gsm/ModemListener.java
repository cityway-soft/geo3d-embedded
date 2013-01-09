package org.avm.device.generic.gsm;

public interface ModemListener {

	public void modemRinging(String origin);

	public void modemSmsIncomming(String bank);

	public void modemHangup();

	public void modemOpened();

	public void modemClosed();

}
