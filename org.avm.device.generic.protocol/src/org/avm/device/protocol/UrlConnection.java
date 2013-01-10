package org.avm.device.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;


public class UrlConnection extends java.net.URLConnection {

	private Connection _conn;
	private ConnectorOutputStream _out;
	private ConnectorInputStream _in;

	public UrlConnection(URL url) {
		super(url);
	}

	public void connect() throws IOException {
		_conn = Connector.open(url.toExternalForm(), Connector.READ_WRITE);
	}

	public OutputStream getOutputStream() throws IOException {
		if (_out == null || !_out.isOpen()) {
			if (_conn == null) {
				_conn = Connector.open(url.toExternalForm(), Connector.WRITE);
			}
			_out = new ConnectorOutputStream((OutputConnection) _conn);
		}
		return _out;
	}

	public InputStream getInputStream() throws IOException {
		if (_in == null || !_in.isOpen()) {
			if (_conn == null) {
				_conn = Connector.open(url.toExternalForm(), Connector.READ);
			}
			_in = new ConnectorInputStream((InputConnection) _conn);
		}
		return _in;
	}
}
