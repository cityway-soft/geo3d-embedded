package org.avm.device.connection.can;

import java.io.IOException;
import java.io.InputStream;

final class CanInputStream extends InputStream {

	private Connection connection;

	private boolean open;

	private byte[] aByte = new byte[1];

	CanInputStream(Connection connection) {
		this.connection = connection;
		open = true;
	}

	public int available() throws IOException {
		if (open)
			return connection.available();

		throw new IOException(com.ibm.oti.util.Msg.getString("K0059"));
	}

	public void close() throws IOException {
		if (open)
			connection.closeStream(true);
		open = false;
	}

	public int read(byte b[], int offset, int length) throws IOException {
		if (open) {
			if (b != null) {
				if (!(offset < 0 || length < 0 || offset > b.length || b.length
						- offset < length))
					return connection.read(b, offset, length);
				throw new IndexOutOfBoundsException();
			}
			throw new NullPointerException();
		}

		throw new IOException(com.ibm.oti.util.Msg.getString("K0059"));
	}

	public int read() throws IOException {
		if (open) {
			synchronized (this) {
				if (connection.read(aByte, 0, 1) > 0)
					return aByte[0] & 0xFF;
			}
			return -1;
		}

		throw new IOException(com.ibm.oti.util.Msg.getString("K0059"));
	}
}
