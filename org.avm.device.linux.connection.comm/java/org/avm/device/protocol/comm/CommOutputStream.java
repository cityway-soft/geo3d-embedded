package org.avm.device.protocol.comm;

import java.io.IOException;
import java.io.OutputStream;

final class CommOutputStream extends OutputStream {

	private Connection _conn;
	private boolean _opened;
	private byte[] _buffer = new byte[1];

	CommOutputStream(Connection connection) {
		this._conn = connection;
		_opened = true;
	}

	public void close() throws IOException {
		if (_opened)
			_conn.closeStream(false);
		_opened = false;
	}

	public void write(byte[] buffer, int offset, int count) throws IOException {
		if (_opened) {
			if (buffer != null) {
				if (!(offset < 0 || count < 0 || offset > buffer.length || buffer.length
						- offset < count))
					_conn.write(buffer, offset, count);
				else
					throw new IndexOutOfBoundsException();
			} else
				throw new NullPointerException();
		} else
			throw new IOException();
	}

	public void write(int oneByte) throws IOException {
		if (_opened) {
			synchronized (this) {
				_buffer[0] = (byte) oneByte;
				_conn.write(_buffer, 0, 1);
			}
		} else
			throw new IOException();
	}
}
