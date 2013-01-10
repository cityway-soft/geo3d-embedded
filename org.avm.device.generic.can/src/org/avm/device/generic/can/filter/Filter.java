package org.avm.device.generic.can.filter;

public interface Filter {

	public boolean execute(byte[] pgn);

}
