package org.avm.device.generic.gsm;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GsmOuputStream extends BufferedOutputStream {

	private static final int DELAY_BETWEEN_CHARS = 0;

	public GsmOuputStream(OutputStream out) {
		super(out);
	}

	public void write(byte[] buffer, int offset, int length) throws IOException {
		super.write(buffer, offset, length);
		flush();
	}

	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	public void write(int oneByte) throws IOException {
		super.write(oneByte);
		flush();
	}

	public void writeln(String data) throws IOException {

		// for (int i = 0; i < data.length(); i++) {
		// if (DELAY_BETWEEN_CHARS > 0) {
		// try {
		// Thread.sleep(DELAY_BETWEEN_CHARS);
		// } catch (Exception e) {
		// }
		// }
		// char c = data.charAt(i);
		// write(c);
		// }

		byte[] buffer = (data + "\r\n").getBytes();
		write(buffer);
		flush();
	}

}
