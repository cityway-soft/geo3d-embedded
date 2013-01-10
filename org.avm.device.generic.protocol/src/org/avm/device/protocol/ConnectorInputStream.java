package org.avm.device.protocol;

import java.io.FilterInputStream;
import java.io.IOException;

import javax.microedition.io.InputConnection;

public class ConnectorInputStream extends FilterInputStream {

	protected InputConnection _conn;
	
	protected boolean _open = false;

	public ConnectorInputStream(InputConnection conn) throws IOException {
		super(conn.openInputStream());
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
	
}
