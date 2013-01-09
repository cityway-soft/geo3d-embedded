package org.avm.device.protocol.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

public class Connection extends URLConnection {

	public final static String OS_NAME = System.getProperty("os.name");
	public final static String OS_VERSION = System.getProperty("os.version");
	public final static boolean is_OS_LINUX = OS_NAME.startsWith("Linux");
	public final static boolean is_OS_WINDOWS = OS_NAME.startsWith("Windows");

	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int READ_WRITE = 3;
	public static final String[][] NO_PARAMETERS = new String[0][0];

	protected static final int UNOPENED = 0;
	protected static final int OPENED = 1;
	protected static final int CLOSED = 2;
	protected static final int DEFAULT_TIMEOUT = 3000;

	protected boolean open, throwTimeout;
	protected int access;
	protected int osHandle;
	protected int inputStatus = UNOPENED, outputStatus = UNOPENED;

	protected OutputStream _out;
	protected InputStream _in;
	protected SerialPort _port;

	public Connection(URL url) {
		super(url);
		open = false;
		throwTimeout = false;
		access = READ_WRITE;
		osHandle = 0;
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
		open(spec, equates, access, false);
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
		if (!open) {
			this.access = access;
			this.throwTimeout = throwTimeout;

			int portNum;

			try {
				portNum = Integer.parseInt(spec);
			} catch (NumberFormatException e) {
				portNum = -1;
			}
			if (portNum < 0)
				throw new IllegalArgumentException(spec);

			int baudrate = 9600, bitsPerChar = 8, stopBits = 1, parity = SerialPort.PARITY_NONE;
			boolean autoRTS = false, autoCTS = false, blocking = true;
			int timeout = 0;

			// go through the parameters and get the necessary values
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
				} else if (key.equals("bitsperchar")) {
					if (value.equals("5"))
						bitsPerChar = 5;
					else if (value.equals("6"))
						bitsPerChar = 6;
					else if (value.equals("7"))
						bitsPerChar = 7;
					else if (value.equals("8"))
						bitsPerChar = 8;
					else
						throw new IllegalArgumentException(value);
				} else if (key.equals("parity")) {
					if (value.equals("odd"))
						parity = SerialPort.PARITY_ODD;
					else if (value.equals("even"))
						parity = SerialPort.PARITY_EVEN;
					else if (value.equals("none"))
						parity = SerialPort.PARITY_NONE;
					else
						throw new IllegalArgumentException(value);
				} else if (key.equals("stopbits")) {
					if (value.equals("1"))
						stopBits = 1;
					else if (value.equals("2"))
						stopBits = 2;
					else
						throw new IllegalArgumentException(value);
				} else if (key.equals("autocts")) {
					if (value.equals("on"))
						autoCTS = true;
					else if (!value.equals("off"))
						throw new IllegalArgumentException(value);
				} else if (key.equals("autorts")) {
					if (value.equals("on"))
						autoRTS = true;
					else if (!value.equals("off"))
						throw new IllegalArgumentException(value);
				} else if (key.equals("blocking")) {
					if (value.equals("off"))
						blocking = false;
					else if (!value.equals("on"))
						throw new IllegalArgumentException(value);
				} else if (key.equals("timeout")) {
					try {
						timeout = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						timeout = -1;
					}
					if (timeout < 0)
						throw new IllegalArgumentException(value);
				} else
					throw new IllegalArgumentException(key);
			}

			// List of allowable baudrates
			switch (baudrate) {
			case 300:
			case 1200:
			case 2400:
			case 4800:
			case 9600:
			case 19200:
			case 38400:
			case 57600:
			case 115200:
			case 230400:
				break;
			default:
				throw new IllegalArgumentException(Integer.toString(baudrate));
			}

			if (blocking && timeout == 0)
				timeout = DEFAULT_TIMEOUT;
			if (!blocking)
				timeout = 0;

			// open the serial port
			osHandle = openImpl(portNum, baudrate, bitsPerChar, stopBits,
					parity, autoRTS, autoCTS, timeout);
			open = true;
		}
	}

	protected int openImpl(int portNum, int baudrate, int bitsPerChar,
			int stopBits, int parity, boolean autoRTS, boolean autoCTS,
			int timeout) throws IOException {

		try {
			String prefix = "";
			if (is_OS_WINDOWS) {
				prefix = "COM";
			} else if (is_OS_LINUX) {
				prefix = "/dev/ttyS";
			}

			CommPortIdentifier identifier = CommPortIdentifier
					.getPortIdentifier(prefix + portNum);
			if (identifier.getPortType() != CommPortIdentifier.PORT_SERIAL) {
				throw new IllegalArgumentException();
			}
			_port = (SerialPort) identifier.open(this.getClass().getName(),
					2000);
			_port.setSerialPortParams(baudrate, bitsPerChar, stopBits, parity);
			_port.enableReceiveTimeout(timeout);
			return 0;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	protected void close() throws IOException {
		if (open) {
			inputStatus = CLOSED;
			outputStatus = CLOSED;
			closeImpl(osHandle);
			open = false;
		}
	}

	protected void closeImpl(int osHandle) throws IOException {
		if (_port != null) {
			try {
				_out.close();
				_in.close();
			} catch (IOException e) {
			} finally {
				_port.close();
			}
		}
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

	protected InputStream openInputStream() throws IOException {
		if (open) {
			if (inputStatus != UNOPENED)
				throw new IOException();

			if (access == READ || access == READ_WRITE) {
				inputStatus = OPENED;
				_in = _port.getInputStream();
				return new CommInputStream(_in,this);
			}
			throw new IOException();
		}

		throw new IOException();
	}

	protected OutputStream openOutputStream() throws IOException {
		if (open) {
			if (outputStatus != UNOPENED)
				throw new IOException();

			if (access == WRITE || access == READ_WRITE) {
				outputStatus = OPENED;
				_out = _port.getOutputStream();
				return new CommOutputStream(_out,this);
			}
			throw new IOException();
		}

		throw new IOException();
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
