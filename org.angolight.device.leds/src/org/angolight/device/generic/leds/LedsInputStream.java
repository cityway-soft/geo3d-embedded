package org.angolight.device.generic.leds;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LedsInputStream extends FilterInputStream {

	public static final int BELL = 0x07;

	private char _buffer[];

	public LedsInputStream(InputStream in) {
		super(in);
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
			case '\r':
			case BELL:
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

		if (((c == -1) ||(c == BELL)) && (offset == 0)) {
			return null;
		}

		return String.copyValueOf(buf, 0, offset);

	}
}
