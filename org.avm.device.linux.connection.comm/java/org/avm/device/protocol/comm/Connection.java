package org.avm.device.protocol.comm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class Connection extends URLConnection {

	public static final int B300 = 300;
	public static final int B600 = 600;
	public static final int B1200 = 1200;
	public static final int B2400 = 2400;
	public static final int B4800 = 4800;
	public static final int B9600 = 9600;
	public static final int B19200 = 19200;
	public static final int B38400 = 38400;
	public static final int B57600 = 57600;
	public static final int B115200 = 115200;
	public static final int B230400 = 230400;

	private final static int STOPBITS1 = 0x0;
	private final static int STOPBITS2 = 0x1;
	private final static int PARITY_ODD = 0x2;
	private final static int PARITY_EVEN = 0x4;
	private final static int AUTORTS = 0x10;
	private final static int AUTOCTS = 0x20;
	private final static int BITSPERCHAR7 = 0x40;
	private final static int BITSPERCHAR8 = 0x80;

	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int READ_WRITE = 3;
	public static final String[][] NO_PARAMETERS = new String[0][0];
	protected static final int UNOPENED = 0;
	protected static final int OPENED = 1;
	protected static final int CLOSED = 2;
	protected static final int DEFAULT_TIMEOUT = 3000;
	private static final int BUFFERSIZE = 255;

	protected int _handle;
	protected int _access;
	protected boolean _opened;
	protected int _inState = UNOPENED, _outState = UNOPENED;
	protected OutputStream _out;
	protected InputStream _in;
	private int _baudrate = B19200;
	private int _bitsperchar = BITSPERCHAR8;
	private int _stopbits = STOPBITS1;
	private int _parity = 0;
	private int _autorts = 0;
	private int _autocts = 0;
	private int _timeout = DEFAULT_TIMEOUT;

	public Connection(URL url) {
		super(url);
		_opened = false;
		_access = READ_WRITE;
		_handle = 0;
	}

	public void connect() throws IOException {

		String spec = url.toExternalForm();
		String scheme = getScheme(spec);
		spec = spec.substring(scheme.length() + 1);
		String[][] equates = NO_PARAMETERS;
		int index = spec.indexOf(';');
		if (index != -1) {
			equates = getParameters(spec.substring(index + 1));
			spec = spec.substring(0, index);
		}
		open(spec, equates, _access, false);
	}

	public OutputStream getOutputStream() throws IOException {
		if (_out == null) {
			try {
				connect();
				return openOutputStream();
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}
		return null;
	}

	public InputStream getInputStream() throws IOException {
		if (_in == null) {
			try {
				connect();
				return openInputStream();
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
		}
		return null;
	}

	protected void open(String spec, String[][] equates, int access,
			boolean throwTimeout) throws IOException {
		if (!_opened) {
			this._access = access;

			int port;

			try {
				port = Integer.parseInt(spec);
			} catch (NumberFormatException e) {
				port = -1;
			}
			if (port < 0)
				throw new IllegalArgumentException(spec);

			parseParameter(equates);

			_handle = openImpl(port, _baudrate, _bitsperchar | _stopbits
					| _parity | _autorts | _autocts, _timeout);
			_opened = true;
		}
	}

	protected abstract int openImpl(int port, int baudrate, int flags,
			int timeout) throws IOException;

	protected void close() throws IOException {
		if (_opened) {
			_inState = CLOSED;
			_outState = CLOSED;
			closeImpl(_handle);
			_opened = false;
		}
	}

	protected abstract void closeImpl(int handle) throws IOException;

	protected int read(byte[] buffer, int offset, int count) throws IOException {
		return readImpl(_handle, buffer, offset, count);
	}

	protected abstract int readImpl(int handle, byte[] buffer, int offset,
			int count) throws IOException;

	public void write(byte[] buffer, int offset, int count) throws IOException {
		writeImpl(_handle, buffer, offset, count);
	}

	protected abstract void writeImpl(int handle, byte[] buffer, int offset,
			int count) throws IOException;

	public int available() throws IOException {
		//return 0;
		return availableImpl(_handle);
	}

	protected abstract int availableImpl(int handle) throws IOException;;

	protected InputStream openInputStream() throws IOException {
		if (_opened) {
			if (_inState != UNOPENED)
				throw new IOException();

			if (_access == READ || _access == READ_WRITE) {
				_inState = OPENED;
				return new BufferedInputStream(new CommInputStream(this),BUFFERSIZE);
			}
			throw new IOException();
		}

		throw new IOException();
	}

	protected OutputStream openOutputStream() throws IOException {
		if (_opened) {
			if (_outState != UNOPENED)
				throw new IOException();

			if (_access == WRITE || _access == READ_WRITE) {
				_outState = OPENED;
				return new BufferedOutputStream(new CommOutputStream(this),BUFFERSIZE);
			}
			throw new IOException();
		}

		throw new IOException();
	}

	protected void closeStream(boolean in) throws IOException {
		boolean close = false;

		if (in) {
			_inState = CLOSED;
			close = (_outState != OPENED);
		} else {
			_outState = CLOSED;
			close = (_inState != OPENED);
		}

		if (close) {
			closeImpl(_handle);
		}
	}

	private static String getScheme(String spec)
			throws IllegalArgumentException {
		if (spec == null)
			throw new IllegalArgumentException();
		int index = spec.indexOf(':');
		if (index == -1)
			throw new IllegalArgumentException();
		String scheme = spec.substring(0, index);
		return scheme;
	}

	private static String[][] getParameters(String params) {
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
					equates[count][1] = equate.substring(eq + 1,
							equate.length());
				} else
					equates[count][0] = equate;
				count++;
			}
			lastIndex = index + 1;
		}
		return equates;
	}

	private void parseParameter(String[][] equates) {
		boolean blocking = true;
		String key = "";
		String value;

		try {
			for (int i = 0; i < equates.length; ++i) {
				key = equates[i][0].toLowerCase();
				value = equates[i][1].toLowerCase();

				if (key.equals("baudrate")) {

					int baudrate = Integer.parseInt(value);
					switch (baudrate) {
					case 300:
						_baudrate = B300;
						break;
					case 600:
						_baudrate = B600;
						break;
					case 1200:
						_baudrate = B1200;
						break;
					case 2400:
						_baudrate = B2400;
						break;
					case 4800:
						_baudrate = B4800;
						break;
					case 9600:
						_baudrate = B9600;
						break;
					case 19200:
						_baudrate = B19200;
						break;
					case 38400:
						_baudrate = B38400;
						break;
					case 57600:
						_baudrate = B57600;
						break;
					case 115200:
						_baudrate = B115200;
						break;
					case 230400:
						_baudrate = B230400;
						break;
					default:
						throw new IllegalArgumentException(key);
					}

				} else if (key.equals("bitsperchar")) {
					int bitsperchar = Integer.parseInt(value);
					switch (bitsperchar) {
					case 7:
						_bitsperchar = BITSPERCHAR7;
						break;
					case 8:
						_bitsperchar = BITSPERCHAR8;
						break;
					default:
						throw new IllegalArgumentException(key);
					}

				} else if (key.equals("parity")) {
					if (value.equals("odd"))
						_parity = PARITY_ODD;
					else if (value.equals("even"))
						_parity = PARITY_EVEN;
					else if (value.equals("none"))
						_parity = 0;
					else
						throw new IllegalArgumentException(key);
				} else if (key.equals("stopbits")) {
					if (value.equals("1"))
						_stopbits = STOPBITS1;
					else if (value.equals("2"))
						_stopbits = STOPBITS2;
					else
						throw new IllegalArgumentException(key);
				} else if (key.equals("autocts")) {
					if (value.equals("on"))
						_autocts = AUTOCTS;
					else if (!value.equals("off"))
						throw new IllegalArgumentException(key);
				} else if (key.equals("autorts")) {
					if (value.equals("on"))
						_autorts = AUTORTS;
					else if (!value.equals("off"))
						throw new IllegalArgumentException(key);
				} else if (key.equals("blocking")) {
					if (value.equals("off"))
						blocking = false;
					else if (!value.equals("on"))
						throw new IllegalArgumentException(key);
				} else if (key.equals("timeout")) {
					try {
						_timeout = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						_timeout = 0;
					}
					if (_timeout < 0)
						throw new IllegalArgumentException(key);
				}
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(key);
		}

		if (blocking && _timeout == 0) {
			_timeout = DEFAULT_TIMEOUT;
		}
		if (!blocking) {
			_timeout = 0;
		}
	}

}
