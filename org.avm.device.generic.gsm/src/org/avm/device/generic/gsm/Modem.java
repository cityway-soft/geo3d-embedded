package org.avm.device.generic.gsm;

import java.io.IOException;

import org.avm.device.gsm.GsmException;
import org.avm.device.gsm.GsmRequest;

public interface Modem {

	public void open(String url) throws IOException;

	public boolean isOpen();

	public void close();

	public String at(GsmRequest command) throws GsmException, IOException;

	public void flush();
}
