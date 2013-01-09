package org.avm.device.protocol.comm;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CommInputStream extends FilterInputStream {
	
	private Connection _conn;

	protected CommInputStream(InputStream in, Connection conn) {
		super(in);
		_conn = conn;
	}

	public void close() throws IOException {
		_conn.closeStream(true);
	}
}
