package org.avm.device.protocol;

import java.io.FilterOutputStream;
import java.io.IOException;

import javax.microedition.io.OutputConnection;

public class ConnectorOutputStream extends FilterOutputStream {

	protected OutputConnection _conn;

	
	protected boolean _open = false;

	public ConnectorOutputStream(OutputConnection conn) throws IOException {
		super(conn.openOutputStream());
		_conn = conn;
		_open = true;
	}

	public void close() throws IOException {
		super.close();
		if (_conn != null) {
			_conn.close();
			_conn = null;
			_open = false;
		}
	}

	public boolean isOpen() {
		return _open;
	}
	
	public void write(byte[] buffer, int offset, int count) throws IOException {
		out.write(buffer, offset, count);
	}

}
