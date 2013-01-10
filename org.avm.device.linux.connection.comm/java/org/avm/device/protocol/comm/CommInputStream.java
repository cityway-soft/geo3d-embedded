package org.avm.device.protocol.comm;

import java.io.IOException;
import java.io.InputStream;

final class CommInputStream extends InputStream {

	private Connection _conn;
	private boolean _opened;
	private byte[] _buffer = new byte[1];

	CommInputStream(Connection conn) {
		this._conn = conn;
		_opened = true;
	}

	public int available() throws IOException {
		if (_opened)
			return _conn.available();

		throw new IOException();
	}

	public void close() throws IOException {
		if (_opened)
			_conn.closeStream(true);
		_opened = false;
	}

	public int read(byte buffer[], int offset, int count) throws IOException {
		if (_opened) {
			if (buffer != null) {
				if (!(offset < 0 || count < 0 || offset > buffer.length || buffer.length
						- offset < count))
					return _conn.read(buffer, offset, count);
				throw new IndexOutOfBoundsException();
			}
			throw new NullPointerException();
		}

		throw new IOException();
	}

	public int read() throws IOException {
		System.out.println("DLA : read...");
		if (_opened) {
			synchronized (this) {
				if (_conn.read(_buffer, 0, 1) > 0){
					System.out.println("DLA : read:"  +  _buffer[0] );
					return _buffer[0] & 0xFF;
				}
			}
			System.out.println("DLA : nothing!");
			return -1;
		}

		throw new IOException();
	}
}
