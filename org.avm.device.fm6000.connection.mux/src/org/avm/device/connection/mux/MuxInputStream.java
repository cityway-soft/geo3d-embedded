package org.avm.device.connection.mux;

import java.io.IOException;
import java.io.InputStream;

final class MuxInputStream extends InputStream {
	private Connection connection;

	private boolean open;

	private byte[] aByte = new byte[1];

	MuxInputStream(Connection connection) {
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
						- offset < length)) {
					// System.out.println("[DSU ] call connection.read offset :
					// " + offset +" length : "+ length);
					int result = connection.read(b, offset, length);
					// System.out.println("[DSU ] "+ result +" bytes read");
					return result;
				}
				throw new IndexOutOfBoundsException();
			}
			throw new NullPointerException();
		}

		throw new IOException(com.ibm.oti.util.Msg.getString("K0059"));
	}

	public int read() throws IOException {
		if (open) {
			synchronized (this) {
				// System.out.println("[DSU ] call connection.read");
				int result = connection.read(aByte, 0, 1);
				// System.out.println("[DSU ] "+ result +" bytes read");
				if (result > 0) {
					return aByte[0] & 0xFF;
				}
			}
			return -1;
		}

		throw new IOException(com.ibm.oti.util.Msg.getString("K0059"));
	}
}
