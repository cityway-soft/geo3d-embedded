package org.avm.elementary.jdb.impl;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

public class JDBCompressedOutputStream extends BufferedOutputStream {

	private Logger _log = Logger.getInstance(JDBCompressedOutputStream.class.getName());

	protected String _filename;

	public JDBCompressedOutputStream(String filename, int size) throws IOException {
		super(null, size);
		_filename = filename;
	}

	private void writeImpl(byte[] buffer, int offset, int length)
			throws IOException {
		_log.debug("compress " + length + " bytes");

		GZIPOutputStream _zout = new GZIPOutputStream(new FileOutputStream(
				_filename, true));
		_zout.write(buffer, offset, length);
		_zout.close();

	}

	public synchronized void flush() throws IOException {
		_log.debug("flush buffer !");
		if (count > 0)
			writeImpl(buf, 0, count);
		count = 0;
	}

	public void close() throws IOException {
		try {
			flush();
		} catch (IOException e) {
		}
	}

	public synchronized void write(byte[] buffer, int offset, int length)
			throws IOException {
		if (buffer != null) {
			// avoid int overflow
			if (0 <= offset && offset <= buffer.length && 0 <= length
					&& length <= buffer.length - offset) {
				if (count == 0 && length >= buf.length) {
					writeImpl(buffer, offset, length);
					return;
				}
				int available = buf.length - count;
				if (length < available)
					available = length;
				if (available > 0) {
					System.arraycopy(buffer, offset, buf, count, available);
					count += available;
				}
				if (count == buf.length) {
					writeImpl(buf, 0, buf.length);
					count = 0;
					if (length > available) {
						offset += available;
						available = length - available;
						if (available >= buf.length) {
							writeImpl(buffer, offset, available);
						} else {
							System.arraycopy(buffer, offset, buf, count,
									available);
							count += available;
						}
					}
				}
			} else
				throw new ArrayIndexOutOfBoundsException();
		} else
			throw new NullPointerException();
	}

	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	public void write(int oneByte) throws IOException {
		byte[] buffer = new byte[1];
		buffer[0] = (byte) oneByte;
		write(buffer, 0, 1);
	}

}
