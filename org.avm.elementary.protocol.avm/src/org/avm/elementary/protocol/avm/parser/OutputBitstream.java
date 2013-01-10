package org.avm.elementary.protocol.avm.parser;

import java.io.IOException;
import java.io.OutputStream;

import flavor.Bitstream;
import flavor.FlIOException;

public class OutputBitstream extends Bitstream {

	public OutputBitstream(OutputStream _out) throws FlIOException {
		this(_out, BUF_LEN);
	}

	public OutputBitstream(OutputStream _out, int _buf_len)
			throws FlIOException {
		super();
		cur_bit = 0;
		total_bits = 0;
		type = BS_OUTPUT;
		buf_len = _buf_len;
		buf = new byte[buf_len];

		out = _out;

		close_fd = false;
	}

	public void flushbits() throws FlIOException {
		super.flushbits();
		try {
			out.flush();
		} catch (IOException e) {
			throw new FlIOException(FlIOException.SYSTEMIOFAILED,
					e.getMessage());
		}
	}

}
