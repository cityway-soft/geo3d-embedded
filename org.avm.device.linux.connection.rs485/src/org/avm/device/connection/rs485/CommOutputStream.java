package org.avm.device.connection.rs485;

/*
 * Licensed Materials - Property of IBM,
 * (c) Copyright IBM Corp. 2000, 2004  All Rights Reserved
 */

import java.io.*;

final class CommOutputStream extends OutputStream {
	private Connection connection;
	private boolean open;
	private byte[] aByte = new byte[1];

	CommOutputStream(Connection connection) {
		this.connection = connection;
		open = true;
	}

	public void close() throws IOException {
		if (open)
			connection.closeStream(false);
		// false indicates outputStream being closed
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
			throw new IOException(com.ibm.oti.util.Msg.getString("K0059"));
	}

	public void write(int oneByte) throws IOException {
		if (open) {
			synchronized (this) {
				aByte[0] = (byte) oneByte;
				connection.write(aByte, 0, 1);
			}
		} else
			throw new IOException(com.ibm.oti.util.Msg.getString("K0059"));
	}
}
