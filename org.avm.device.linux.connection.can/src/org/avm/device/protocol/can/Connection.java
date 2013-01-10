package org.avm.device.protocol.can;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Connection extends URLConnection {

	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int READ_WRITE = 3;
	private static final int DEFAULT_TIMEOUT = 100;
	private static final int DEFAULT_BAUDRATE = 125000;
	private static final int UNOPENED = 0;
	private static final int OPENED = 1;
	private static final int CLOSED = 2;
	public static final String[][] NO_PARAMETERS = new String[0][0];

	private static final int FRAME_LEN = 14;
	private OutputStream _out;
	private InputStream _in;

	private boolean open, throwTimeout;
	private int access;
	private int osHandle;
	private int inputStatus = UNOPENED, outputStatus = UNOPENED;
	private int bufferSize;

	static {
		try {
			System.out.println("[DSU] load can library ");
			System.loadLibrary("can");
		} catch (Throwable e) {
			System.out.println("[DSU] echec load  " + e.getMessage());
		}
	}

	public Connection(URL url) {
		super(url);
		open = false;
		throwTimeout = false;
		access = READ_WRITE;
		osHandle = 0;
	}

	public Object getContent() throws IOException {
		Object result = null;
		byte[] b = new byte[FRAME_LEN];

		InputStream in = getInputStream();
		if (in != null) {
			result = (in.read(b) != -1) ? b : null;
		}
		return result;
	}

	public void connect() throws IOException {
		open(url.toExternalForm());
	}

	public OutputStream getOutputStream() throws IOException {
		if (_out == null) {
			try {
				_out = openOutputStream();
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}
		return _out;
	}

	public InputStream getInputStream() throws IOException {
		if (_in == null) {
			try {
				_in = openInputStream();
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
		}
		return _in;
	}

	protected InputStream openInputStream() throws IOException {
		if (!open) {
			connect();
		}
		if (open) {
			if (inputStatus != UNOPENED)
				throw new IOException();

			if (access == READ || access == READ_WRITE) {
				inputStatus = OPENED;
				return new BufferedInputStream(new CanInputStream(this),
						bufferSize);
			}
			throw new IOException();
		}

		throw new IOException();
	}

	protected OutputStream openOutputStream() throws IOException {
		if (!open) {
			connect();
		}

		if (open) {
			if (outputStatus != UNOPENED)
				throw new IOException();

			if (access == WRITE || access == READ_WRITE) {
				outputStatus = OPENED;
				return new CanOutputStream(this);
			}
			throw new IOException();
		}

		throw new IOException();
	}

	protected void closeStream(boolean inputStream) throws IOException {

		boolean closeConn = false;

		if (inputStream) {
			inputStatus = CLOSED;
			closeConn = (outputStatus != OPENED);
		} else {
			outputStatus = CLOSED;
			closeConn = (inputStatus != OPENED);
		}

		if (closeConn) {
			closeImpl(osHandle);
		}

	}

	protected void open(String spec) throws IOException {
		open(spec, READ_WRITE, false);
	}

	protected void open(String spec, int access) throws IOException {
		open(spec, access, false);
	}

	protected void open(String spec, int access, boolean timeout)
			throws IOException {
		String scheme = getScheme(spec);
		if (access != READ && access != WRITE && access != READ_WRITE)
			throw new IllegalArgumentException();

		spec = spec.substring(scheme.length() + 1);

		String[][] equates = NO_PARAMETERS;
		int index = spec.indexOf(';');
		if (index != -1) {
			equates = getParameters(spec.substring(index + 1));
			spec = spec.substring(0, index);
		}
		open(spec, equates, access, timeout);

	}

	protected void open(String spec, String[][] equates, int access,
			boolean throwTimeout) throws IOException {
		this.access = access;
		this.throwTimeout = throwTimeout;
		int port;
		int timeout = DEFAULT_TIMEOUT;
		int baudrate = DEFAULT_BAUDRATE;
		boolean blocking = true;
		ArrayList filterList = new ArrayList();
		ArrayList maskList = new ArrayList();

		try {
			port = Integer.parseInt(spec);
		} catch (NumberFormatException e) {
			port = -1;
		}
		if (port < 0)
			throw new IllegalArgumentException(spec);

		String key, value;
		for (int i = 0; i < equates.length; ++i) {
			key = equates[i][0].toLowerCase();
			value = equates[i][1].toLowerCase();

			if (key.equals("baudrate")) {
				try {
					baudrate = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(value);
				}
			} else if (key.equals("buffersize")) {
				try {
					bufferSize = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(value);
				}
			} else if (key.equals("blocking")) {
				if (value.equals("off"))
					blocking = false;
				else if (!value.equals("on"))
					throw new IllegalArgumentException(value);
			} else if (key.equals("timeout")) {
				try {
					timeout = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					timeout = DEFAULT_TIMEOUT;
				}
				if (timeout < 0)
					throw new IllegalArgumentException(value);
			} else if (key.startsWith("filter")) {
				int index = Integer.parseInt(key.substring("filter".length()));
				filterList.add(index, new Long(Long.parseLong(value, 16)));
			} else if (key.startsWith("mask")) {
				int index = Integer.parseInt(key.substring("mask".length()));
				maskList.add(index, new Long(Long.parseLong(value, 16)));
			} else
				throw new IllegalArgumentException(key);
		}

		if (blocking && timeout == 0)
			timeout = DEFAULT_TIMEOUT;
		if (!blocking)
			timeout = 0;

		int[] filter = new int[filterList.size()];
		int[] mask = new int[filterList.size()];

		for (int i = 0; i < filter.length; i++) {
			filter[i] = ((Long) filterList.get(i)).intValue();
			mask[i] = ((Long) maskList.get(i)).intValue();
		}

		osHandle = openImpl(port, baudrate, timeout, filter, mask);

		open = true;
	}

	private native int openImpl(int portNum, int baudrate, int timeout,
			int[] filter, int[] mask) throws IOException;

	public void close() throws IOException {
		if (open) {
			inputStatus = CLOSED;
			outputStatus = CLOSED;
			closeImpl(osHandle);
			open = false;
		}
	}

	private native void closeImpl(int osHandle) throws IOException;

	int read(byte[] b, int off, int len) throws IOException {
		if (len != 0) {
			int bytesRead = readImpl(osHandle, b, off, len);
			if ((bytesRead > 0) || (!throwTimeout))
				return bytesRead;
			InterruptedIOException e = new InterruptedIOException();
			e.bytesTransferred = bytesRead;
			throw e;
		}
		return 0;
	}

	private native int readImpl(int osHandle, byte[] b, int off, int len)
			throws IOException;

	int write(byte[] b, int off, int len) throws IOException {
		if (len != 0) {
			int bytesWritten = writeImpl(osHandle, b, off, len);
			if (!throwTimeout || (bytesWritten == len))
				return bytesWritten;
			InterruptedIOException e = new InterruptedIOException();
			e.bytesTransferred = bytesWritten;
			throw e;
		}
		return 0;
	}

	private native int writeImpl(int osHandle, byte[] b, int off, int len)
			throws IOException;

	int available() throws IOException {
		return availableImpl(osHandle);
	}

	private native int availableImpl(int osHandle) throws IOException;

	private String getScheme(String spec) throws IllegalArgumentException {
		if (spec == null)
			throw new IllegalArgumentException();
		int index = spec.indexOf(':');
		if (index == -1)
			throw new IllegalArgumentException();
		String scheme = spec.substring(0, index);
		return scheme;
	}

	private String[][] getParameters(String params) {
		int index, lastIndex = 0, count = 0;
		while (lastIndex < params.length()) {
			index = params.indexOf(';', lastIndex);
			if (index == -1)
				index = params.length();
			if (index > lastIndex)
				count++;
			lastIndex = index + 1;
		}
		String[][] equates = new String[count][2];
		count = 0;
		lastIndex = 0;
		while (lastIndex < params.length()) {
			index = params.indexOf(';', lastIndex);
			if (index == -1)
				index = params.length();
			String equate = params.substring(lastIndex, index);
			if (index > lastIndex) {
				int eq = equate.indexOf('=');
				if (eq != -1) {
					equates[count][0] = equate.substring(0, eq);
					equates[count][1] = equate.substring(eq + 1, equate
							.length());
				} else
					equates[count][0] = equate;
				count++;
			}
			lastIndex = index + 1;
		}
		return equates;
	}

}
