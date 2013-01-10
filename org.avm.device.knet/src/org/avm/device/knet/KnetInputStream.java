package org.avm.device.knet;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class KnetInputStream extends FilterInputStream {

	private byte boundary = '\0';

	public KnetInputStream(InputStream in) {
		super(new PushbackInputStream(in, 8192));
	}

	public int read(byte[] buf, int off, int len) throws IOException {

		int avail = in.available();
		int copy = 0;

		if (avail > 0) {
			copy = Math.min(len, avail);
		} else {
			copy = len;
		}

		byte[] buffer = new byte[copy];
		int n = in.read(buffer, 0, copy);
		if (n == -1) {
			return -1;
		}

		String str = new String(buffer, 0, n);
		int index = str.indexOf(boundary);
		if (index == -1) {
			copy = n;
			System.arraycopy(buffer, 0, buf, off, copy);
		} else {
			copy = index;
			int ulen = n - index - 1;
			int uoff = index + 1;
			((PushbackInputStream) in).unread(buffer, uoff, ulen);
			System.arraycopy(buffer, 0, buf, off, copy);
		}

		return copy;
	}

}
