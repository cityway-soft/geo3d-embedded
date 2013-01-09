package org.avm.device.protocol.rs485;

import java.io.IOException;
import java.net.URL;

import org.avm.device.protocol.comm.Connection;

public class Rs485Connection extends Connection {

	static {
		try {
			System.out.println("[DSU] load rs485 library ");
			System.loadLibrary("rs485");
		} catch (Throwable e) {
			System.out.println("[DSU] echec load  " + e.getMessage());
		}
	}

	public Rs485Connection(URL url) {
		super(url);
	}

	protected native int openImpl(int port, int baudrate, int flags, int timeout)
			throws IOException;

	protected native void closeImpl(int handle) throws IOException;

	protected native int readImpl(int handle, byte[] buffer, int off, int len)
			throws IOException;

	protected native void writeImpl(int handle, byte[] buffer, int off, int len)
			throws IOException;

	protected native int availableImpl(int handle) throws IOException;

}
