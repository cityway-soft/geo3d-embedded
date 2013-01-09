package org.avm.elementary.protocol.avm.parser;

import java.io.IOException;
import java.io.InputStream;

import flavor.Bitstream;
import flavor.FlIOException;

public class InputBitstream extends Bitstream {

	public InputBitstream(InputStream _in) throws FlIOException {
		this(_in, BUF_LEN);
	}

	public InputBitstream(InputStream _in, int _buf_len) throws FlIOException {
		cur_bit = 0;
		total_bits = 0;
		type = BS_INPUT;
		buf_len = _buf_len;
		buf = new byte[buf_len];

		try {
			in = _in;
			cur_bit = BUF_LEN << 3; // Fake we are at the eof of buffer
			fill_buf();
		} catch (IOException e) {
			throw new FlIOException(FlIOException.FILEOPENFAILED, e.toString());
		}

		close_fd = false;
	}

}
