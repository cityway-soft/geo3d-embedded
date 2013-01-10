package org.avm.device.generic.gsm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class GsmInputStream extends PushbackInputStream {

	private char _buffer[];

	public GsmInputStream(InputStream in) {
		super(in);
	}

	public GsmInputStream(InputStream in, int size) {
		super(in, size);
	}

	public String readln() throws IOException {

		char buf[] = _buffer;

		if (buf == null) {
			buf = _buffer = new char[128];
		}

		int room = buf.length;
		int offset = 0;
		int c = -1;

		loop: while (true) {

			switch (c = read()) {

			case -1:
			case '\n':
				break loop;

			case '\r':

				int c2 = read();
				if ((c2 != '\n') && (c2 != -1)) {
					unread(c2);
				} else {
					if (available() == 0) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							System.out.println(e.toString());
						}
					}
					if (available() > 0) {
						int c3 = read();
						if ((c3 != '>') && (c3 != -1)) {
							unread(c3);
						} else {
							if (--room < 0) {
								buf = new char[offset + 128];
								room = buf.length - offset - 1;
								System.arraycopy(_buffer, 0, buf, 0, offset);
								_buffer = buf;
							}
							buf[offset++] = (char) c3;
						}
					}

				}
				break loop;

			default:
				if (--room < 0) {
					buf = new char[offset + 128];
					room = buf.length - offset - 1;
					System.arraycopy(_buffer, 0, buf, 0, offset);
					_buffer = buf;
				}
				buf[offset++] = (char) c;
				break;
			}
		}

		if ((c == -1) && (offset == 0)) {
			return null;
		}

		return String.copyValueOf(buf, 0, offset);

	}
}
