package org.avm.device.protocol.vcan;

import java.io.IOException;
import java.io.OutputStream;

final class CanOutputStream extends OutputStream {
	private Connection connection;

	private boolean open;

	private byte[] aByte = new byte[1];

	CanOutputStream(Connection connection) {
		this.connection = connection;
		open = true;
	}

	public void close() throws IOException {
		if (open)
			connection.closeStream(false);
		open = false;
	}

	public void write(byte[] b, int offset, int length) throws IOException {
		if (open) {
			if (b != null) {
				if (!(offset < 0 || length < 0 || offset > b.length || b.length
						- offset < length))
					connection.write(b, offset, length);
				else
					throw new IndexOutOfBoundsException();
			} else
				throw new NullPointerException();
		} else
			throw new IOException();
	}

	public void write(int oneByte) throws IOException {
		if (open) {
			synchronized (this) {
				aByte[0] = (byte) oneByte;
				connection.write(aByte, 0, 1);
			}
		} else
			throw new IOException();
	}
}
