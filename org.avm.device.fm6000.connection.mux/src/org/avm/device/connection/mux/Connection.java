package org.avm.device.connection.mux;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.ibm.oti.connection.ConnectionUtil;
import com.ibm.oti.connection.CreateConnection;
import com.ibm.oti.connection.DataConnection;

public class Connection extends DataConnection implements CreateConnection,
		StreamConnection {
	private static final int DEFAULT_TIMEOUT = 3000;

	private static final int PARITY_NONE = 0;

	private static final int PARITY_ODD = 1;

	private static final int PARITY_EVEN = 2;

	private static final int UNOPENED = 0;

	private static final int OPEN = 1;

	private static final int CLOSED = 2;

	private boolean open, throwTimeout;

	private boolean implOpen;

	private int access;

	private int osHandle;

	private int timeout;

	private int inputStatus = UNOPENED, outputStatus = UNOPENED;

	public Connection() {

		open = false;
		implOpen = false;
		throwTimeout = false;
		access = Connector.READ_WRITE;
		timeout = 5000;

		osHandle = 0;
	}

	public void setParameters(String spec, int access, boolean timeout)
			throws IOException {

		String[][] equates = ConnectionUtil.NO_PARAMETERS;
		int index = spec.indexOf(';');
		if (index != -1) {
			equates = ConnectionUtil.getParameters(spec.substring(index + 1));
			spec = spec.substring(0, index);
		}
		setParameters(spec, equates, access, timeout);
	}

	private void setParameters(String spec, String[][] equates, int access,
			boolean throwTimeout) throws IOException {
		this.access = access;
		this.throwTimeout = throwTimeout;

		String port = spec;

		int baudrate = 9600, bitsPerChar = 8, stopBits = 1, parity = PARITY_NONE;
		boolean autoRTS = false, autoCTS = false, blocking = true;

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
					parity = PARITY_ODD;
				else if (value.equals("even"))
					parity = PARITY_EVEN;
				else if (value.equals("none"))
					parity = PARITY_NONE;
				else
					throw new IllegalArgumentException(value);
			} else if (key.equals("stopbits")) {
				if (value.equals("1"))
					stopBits = 0;
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
			break;
		default:
			throw new IllegalArgumentException(Integer.toString(baudrate));
		}

		if (blocking && timeout == 0)
			timeout = DEFAULT_TIMEOUT;
		if (!blocking)
			timeout = 0;

		// open the serial port
		osHandle = openImpl(port);
		configureImpl(osHandle, baudrate, bitsPerChar, stopBits, parity,
				autoRTS, autoCTS, timeout);
		open = true;
		implOpen = true;
	}

	private native int openImpl(String port) throws IOException;

	private native void configureImpl(int osHandle, int baudrate,
			int bitsPerChar, int stopBits, int parity, boolean autoRTS,
			boolean autoCTS, int timeout) throws IOException;

	public void close() throws IOException {
		if (open) {

			if (inputStatus != OPEN && outputStatus != OPEN && implOpen) {
				implOpen = false;
				closeImpl(osHandle);
			}
			open = false;
		}
	}

	private native void closeImpl(int osHandle) throws IOException;

	void closeStream(boolean inputStream) throws IOException {

		boolean closeConn = false;

		if (inputStream) {
			inputStatus = CLOSED;
			closeConn = (outputStatus == CLOSED || (outputStatus == UNOPENED && open == false));
		} else {
			outputStatus = CLOSED;
			closeConn = (inputStatus == CLOSED || (inputStatus == UNOPENED && open == false));
		}

		if (closeConn) {
			implOpen = false;
			closeImpl(osHandle);
		}

	}

	public InputStream openInputStream() throws IOException {
		if (open) {
			if (inputStatus != UNOPENED)
				throw new IOException(com.ibm.oti.util.Msg.getString("K0192"));

			if (access == Connector.READ || access == Connector.READ_WRITE) {
				inputStatus = OPEN;
				return new MuxInputStream(this);
			}
			throw new IOException(com.ibm.oti.util.Msg.getString("K00a9"));
		}

		throw new IOException(com.ibm.oti.util.Msg.getString("K00ac"));
	}

	public OutputStream openOutputStream() throws IOException {
		if (open) {
			if (outputStatus != UNOPENED)
				throw new IOException(com.ibm.oti.util.Msg.getString("K0192"));

			if (access == Connector.WRITE || access == Connector.READ_WRITE) {
				outputStatus = OPEN;
				return new MuxOutputStream(this);
			}
			throw new IOException(com.ibm.oti.util.Msg.getString("K00aa"));
		}

		throw new IOException(com.ibm.oti.util.Msg.getString("K00ac"));
	}

	int read(byte[] b, int off, int len) throws IOException {

		if (len != 0) {
			// save the start time of the read call
			long startTime = System.currentTimeMillis();
			while (true) {
				// System.out.println("[DSU ] call readImpl offset : " + off +"
				// length : "+ len);
				int bytesRead = readImpl(osHandle, b, off, len);
				// System.out.println("[DSU ] "+ bytesRead +" bytes read");
				if (bytesRead > 0)
					return bytesRead;
				// if there were no bytes read but have not passed the timeout
				// time try again
				if (bytesRead == 0
						&& (System.currentTimeMillis() - startTime < timeout))
					continue;
				if (!throwTimeout)
					return bytesRead;
				InterruptedIOException e = new InterruptedIOException(
						com.ibm.oti.util.Msg.getString("K00df"));
				e.bytesTransferred = bytesRead;
				throw e;
			}
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
			InterruptedIOException e = new InterruptedIOException(
					com.ibm.oti.util.Msg.getString("K00df"));
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

	static {
		try {
			// System.out.println("[DSU] load MuxConnector library ");
			System.loadLibrary("MuxConnector");
		} catch (Exception e) {
			System.out.println("[DSU] echec load  " + e.getMessage());
		}
	}

}
