package org.avm.device.protocol.rs485;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.avm.device.protocol.comm.CommOutputStream;

public class Connection extends org.avm.device.protocol.comm.Connection {

	public Connection(URL url) {
		super(url);
	}

	protected OutputStream openOutputStream() throws IOException {
		if (open) {
			if (outputStatus != UNOPENED)
				throw new IOException();

			if (access == WRITE || access == READ_WRITE) {
				outputStatus = OPENED;
				return new CommOutputStream(_port.getOutputStream(),this) {

					public void write(byte[] buffer, int offset, int count)
							throws IOException {
						try {
							Thread.sleep(200);
							_port.setRTS(false);
							out.write(buffer, offset, count);
							Thread.sleep(200 + count * 10);
							_port.setRTS(true);
						} catch (InterruptedException e) {

						}
					}

					public void write(int oneByte) throws IOException {
						byte[] buffer = new byte[1];
						buffer[0] = (byte) oneByte;
						super.write(buffer);
					}
					
				};
			}
			throw new IOException();
		}

		throw new IOException();
	}

}
