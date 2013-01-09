package org.avm.device.protocol.comm;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CommOutputStream extends FilterOutputStream{
	private Connection _conn;
	
	public CommOutputStream(OutputStream out, Connection conn) {
		super(out);
		_conn = conn;
	}

	public void close() throws IOException {
		_conn.closeStream(false);
	}
}
