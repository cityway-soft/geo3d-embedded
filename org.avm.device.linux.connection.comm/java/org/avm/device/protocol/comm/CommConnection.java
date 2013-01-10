package org.avm.device.protocol.comm;

import java.io.IOException;
import java.net.URL;

public class CommConnection extends Connection {

	static {
		try {
			System.out.println("[DSU] load comm library ");
			System.loadLibrary("comm");
		} catch (Throwable e) {
			System.out.println("[DSU] echec load  " + e.getMessage());
		}
	}

	public CommConnection(URL url) {
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
