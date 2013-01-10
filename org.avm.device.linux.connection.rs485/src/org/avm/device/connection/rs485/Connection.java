package org.avm.device.connection.rs485;

/*
 * Licensed Materials - Property of IBM,
 * (c) Copyright IBM Corp. 2000, 2004  All Rights Reserved
 */
import java.io.*;

import javax.microedition.io.*;
import com.ibm.oti.connection.*;

/**
 * Implements a serial port connection. Accepts the following parameters:
 * <UL>
 * <LI>baudrate - set baud rate for the serial port, defaults to 9600</LI>
 * <LI>bitsperchar - 5,6,7 or 8, sets the number of bits per byte, defaults to
 * 8</LI>
 * <LI>stopbits - 1 or 2, sets the number of stop bits, defaults to 1</LI>
 * <LI>parity - none, even or odd, sets the parity scheme to use, defaults to
 * none</LI>
 * <LI>autorts - off/on, enables input hardware flow control, defaults to off</LI>
 * <LI>autocts - off/on, enables output hardware flow control, defaults to off</LI>
 * <LI>blocking - off/on, enables read/write blocking, defaults to on</LI>
 * <LI>timeout - if blocking=on, timeout is the number of milliseconds until
 * the read or write function will timeout, defaults to 3000</LI>
 * </UL>
 * 
 * @author OTI
 * @version initial
 * 
 * @see javax.microedition.io.StreamConnection
 */
public class Connection extends DataConnection implements CreateConnection,
		StreamConnection {
	private static final int DEFAULT_TIMEOUT = 3000; // in milliseconds
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

	/**
	 * Constructs a new instance of this class.
	 * 
	 * @author OTI
	 * @version initial
	 */
	public Connection() {

		open = false;
		implOpen = false;
		throwTimeout = false;
		access = Connector.READ_WRITE;
		timeout = 5000;

		osHandle = 0;
	}

	/**
	 * Passes the parameters from the Connector.open() method to this object.
	 * Protocol used by MIDP 1.0
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param spec
	 *            String The address passed to Connector.open()
	 * @param access
	 *            int The type of access this Connection is granted (READ,
	 *            WRITE, READ_WRITE)
	 * @param timeout
	 *            boolean A boolean indicating whether or not the caller to
	 *            Connector. open() wants timeout exceptions
	 * @exception IOException
	 *                If an error occured opening and configuring serial port.
	 * 
	 * @see javax.microedition.io.Connector
	 */
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
	
	/**
	 * Passes the parameters from the Connector.open() method to this
	 * object. Protocol used by MIDP 2.0
	 *
	 * @author		OTI
	 * @version		initial
	 *
	 * @param		spec String
	 *					The address passed to Connector.open()
	 * @param		access int
	 *					The type of access this Connection is
	 *					granted (READ, WRITE, READ_WRITE)
	 * @param		timeout boolean
	 *					A boolean indicating whether or not the
	 *					caller to Connector.open() wants timeout
	 *					exceptions
	 * @exception	IOException
	 *					If an error occured opening and configuring
	 *					serial port.
	 *
	 * @see javax.microedition.io.Connector
	 */
	public javax.microedition.io.Connection setParameters2(String spec, int access, boolean timeout) throws IOException {

		setParameters(spec, access, timeout);
		return this;
	}
	

	/**
	 * Passes the parameters from the Connector.open() method to this object.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param spec
	 *            String The address passed to Connector.open()
	 * @param equates
	 *            String[][] An 2-dimensional array of strings that contain the
	 *            parameters and their keys passed to Connector.open()
	 * @param access
	 *            int The type of access this Connection is granted (READ,
	 *            WRITE, READ_WRITE)
	 * @param throwTimeout
	 *            boolean A boolean indicating whether or not the caller to
	 *            Connector.open() wants timeout exceptions or not
	 * @exception IOException
	 *                If an error occured opening and configuring serial port.
	 * 
	 * @see javax.microedition.io.Connector
	 */
	private void setParameters(String spec, String[][] equates, int access,
			boolean throwTimeout) throws IOException {
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

		int baudrate = 9600, bitsPerChar = 8, stopBits = 1, parity = PARITY_NONE;
		boolean autoRTS = false, autoCTS = false, blocking = true;

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
					parity = PARITY_ODD;
				else if (value.equals("even"))
					parity = PARITY_EVEN;
				else if (value.equals("none"))
					parity = PARITY_NONE;
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
			break;
		default:
			throw new IllegalArgumentException(Integer.toString(baudrate));
		}

		if (blocking && timeout == 0)
			timeout = DEFAULT_TIMEOUT;
		if (!blocking)
			timeout = 0;

		// open the serial port
		System.out.println("[DSU] Connection.setParameters()");
		osHandle = openImpl(portNum);
		configureImpl(osHandle, baudrate, bitsPerChar, stopBits, parity,
				autoRTS, autoCTS, timeout);
		open = true;
		implOpen = true;
	}

	/**
	 * Talks to the hardware and attempts to open the serial port
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param portNum
	 *            int The number of the serial port to open
	 * @return int A handle to the serial port for the OS
	 * @exception IOException
	 *                If an error occured opening the port
	 */
	private native int openImpl(int portNum) throws IOException;

	/**
	 * Talks to the hardware and attempts to configure the serial port.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param osHandle
	 *            int The OS handle or file descriptor for the serial port
	 * @param baudrate
	 *            int The baudrate at which to set the serial port
	 * @param bitsPerChar
	 *            int The number of bits for every byte (5, 6, 7, or 8)
	 * @param stopbits
	 *            int The number of stop bits (1 or 2)
	 * @param parity
	 *            int The parity to check for can be PARITY_NONE, PARITY_ODD or
	 *            PARITY_EVEN
	 * @param autoRTS
	 *            boolean true if the hardware should handle raising and
	 *            lowering the RTS line
	 * @param autoCTS
	 *            boolean true if the hardware should examine the CTS line
	 * @param timeout
	 *            int timeout value in milliseconds for read and write methods,
	 *            if 0 methods will not block
	 * @exception IOException
	 *                If an error occured configuring serial port
	 */
	private native void configureImpl(int osHandle, int baudrate,
			int bitsPerChar, int stopBits, int parity, boolean autoRTS,
			boolean autoCTS, int timeout) throws IOException;

	/**
	 * Closes the connection. Any open streams remain accessible but the
	 * connection itself does not.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @exception IOException
	 *                If an error occured closing the serial port.
	 * 
	 * @see javax.microedition.io.Connection
	 */
	public void close() throws IOException {
		if (open) {

			// only "actually" close the connection if there are no open streams

			if (inputStatus != OPEN && outputStatus != OPEN && implOpen) {
				implOpen = false;
				closeImpl(osHandle);
			}
			open = false;
		}
	}

	/**
	 * Closes the serial port.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param osHandle
	 *            int The OS handle or file descriptor for the serial port
	 * @exception IOException
	 *                If an error occured closing the serial port
	 * 
	 * @see javax.microedition.io.Connection
	 */
	private native void closeImpl(int osHandle) throws IOException;

	/**
	 * Closes the connection if no streams are still open.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @exception IOException
	 *                If an error occurred closing the serial port
	 */
	void closeStream(boolean inputStream) throws IOException {
		// This method should be called by CommInputStream or
		// CommOutputStream when close is called.
		// inputStream == true indicates we are closing an inputstream,
		// otherwise an outputstream
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

	/**
	 * Returns an InputStream that reads from the serial port opened by this
	 * Connection.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @return IntputStream An InputStream that reads from this serial port
	 * @exception IOException
	 *                If an error occured trying to create the InputStream
	 */
	public InputStream openInputStream() throws IOException {
		if (open) {
			if (inputStatus != UNOPENED)
				throw new IOException(com.ibm.oti.util.Msg.getString("K0192"));

			if (access == Connector.READ || access == Connector.READ_WRITE) {
				inputStatus = OPEN;
				return new CommInputStream(this);
			}
			throw new IOException(com.ibm.oti.util.Msg.getString("K00a9"));
		}

		throw new IOException(com.ibm.oti.util.Msg.getString("K00ac"));
	}

	/**
	 * Returns an OutputStream that writes to the serial port opened by this
	 * Connection.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @return OutputStream An OutputStream that writes to this serial port
	 * @exception IOException
	 *                If an error occured trying to create the OutputStream
	 * 
	 * @see javax.microedition.io.Connector
	 */
	public OutputStream openOutputStream() throws IOException {
		if (open) {
			if (outputStatus != UNOPENED)
				throw new IOException(com.ibm.oti.util.Msg.getString("K0192"));

			if (access == Connector.WRITE || access == Connector.READ_WRITE) {
				outputStatus = OPEN;
				return new CommOutputStream(this);
			}
			throw new IOException(com.ibm.oti.util.Msg.getString("K00aa"));
		}

		throw new IOException(com.ibm.oti.util.Msg.getString("K00ac"));
	}

	/**
	 * Reads <code>len</code> bytes from the serial port and places them in
	 * byte array <code>b</code> at an offset of <code>off</code>.
	 * <p>
	 * Returns the number of bytes read.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param b
	 *            byte[] The byte array in which to store the read bytes
	 * @param off
	 *            int An offset at which to start storing the bytes in the byte
	 *            array
	 * @param len
	 *            int The number of bytes to read
	 * @return int The number of bytes read
	 * @exception IOException
	 *                If an error occured trying to read from the serial port
	 */
	int read(byte[] b, int off, int len) throws IOException {
		// checking if b is null and making sure off and len
		// are within bounds is done in CommInputStream
		// as the visibility of this method is package,
		// no one else will call this method.

		if (len != 0) {
			// save the start time of the read call
			long startTime = System.currentTimeMillis();
			while (true) {
				int bytesRead = readImpl(osHandle, b, off, len);
				if (bytesRead > 0)
					return bytesRead;
				// if there were no bytes read but have not passed the timeout
				// time
				// try again
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

	/**
	 * Reads <code>len</code> bytes from the serial port and places them in
	 * byte array <code>b</code> at an offset of <code>off</code>.
	 * <p>
	 * Returns the number of bytes read.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param osHandle
	 *            int The OS handle or file descriptor for the serial port
	 * @param b
	 *            byte[] The byte array in which to store the read bytes
	 * @param off
	 *            int An offset at which to start storing the bytes in the byte
	 *            array
	 * @param len
	 *            int The number of bytes to read
	 * @return int The number of bytes read
	 * @exception IOException
	 *                If an error occured trying to read from the serial port
	 */
	private native int readImpl(int osHandle, byte[] b, int off, int len)
			throws IOException;

	/**
	 * Writes <code>len</code> bytes to the serial port from the byte array
	 * <code>b</code> starting at an offset of <code>off</code>.
	 * <p>
	 * Returns the number of bytes writen.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param b
	 *            byte[] The bytes to be written to the serial port
	 * @param off
	 *            int An offset at which to start reading the bytes from the
	 *            byte array
	 * @param len
	 *            int The number of bytes to write
	 * @return int The number of bytes written
	 * @exception IOException
	 *                If an error occured trying to write to the serial port
	 */
	int write(byte[] b, int off, int len) throws IOException {
		// checking if b is null and making sure off and len
		// are within bounds is done in CommInputStream
		// as the visibility of this method is package then
		// no one else will call this method directly

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

	/**
	 * Writes <code>len</code> bytes to the serial port from the byte array
	 * <code>b</code> starting at an offset of <code>off</code>.
	 * <p>
	 * Returns the number of bytes writen.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param osHandle
	 *            int The OS handle or file descriptor for the serial port
	 * @param b
	 *            byte[] The bytes to be written to the serial port
	 * @param off
	 *            int An offset at which to start reading the bytes from the
	 *            byte array
	 * @param len
	 *            int The number of bytes to write
	 * @return int The number of bytes written
	 * @exception IOException
	 *                If an error occured trying to write to the serial port
	 */
	private native int writeImpl(int osHandle, byte[] b, int off, int len)
			throws IOException;

	/**
	 * Returns the number of bytes available in the recieve queue.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @return int The number of bytes in the recieve queue
	 * @exception IOException
	 *                If an error occured while checking the recieve queue
	 */
	int available() throws IOException {
		return availableImpl(osHandle);
	}

	/**
	 * Returns the number of bytes available in the recieve queue.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param osHandle
	 *            int The OS handle or file descriptor for the serial port
	 * @return int The number of bytes in the recieve queue
	 * @exception IOException
	 *                If an error occured while checking the recieve queue
	 */
	private native int availableImpl(int osHandle) throws IOException;

	static {
		try {
			System.out.println("[DSU] load rs485 library ");
			System.loadLibrary("rs485");
		} catch (Exception e) {
			System.out.println("[DSU] echec load  " + e.getMessage());
		}
	}

	
}