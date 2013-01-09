package org.avm.device.connection.can;

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
    private static final int DEFAULT_TIMEOUT = 100;

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

	int port;

	try {
	    port = Integer.parseInt(spec);
	} catch (NumberFormatException e) {
	    port = -1;
	}
	if (port < 0)
	    throw new IllegalArgumentException(spec);

	int baudrate = 125000;
	int propagation = 25;

	int[] filter = new int[16];
	int[] mask = new int[16];

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
	    } else if (key.startsWith("filter")) {
		int index = Integer.parseInt(key.substring("filter".length()));
		filter[index] = Integer.parseInt(value, 16);
	    } else if (key.startsWith("mask")) {
		int index = Integer.parseInt(key.substring("mask".length()));
		mask[index] = Integer.parseInt(value, 16);
	    } else if (key.equals("timeout")) {
		try {
		    timeout = Integer.parseInt(value);
		} catch (NumberFormatException e) {
		    timeout = DEFAULT_TIMEOUT;
		}
		if (timeout < 0)
		    throw new IllegalArgumentException(value);
	    } else
		throw new IllegalArgumentException(key);
	}

	// open the serial port
	osHandle = openImpl(port);
	configureImpl(osHandle, baudrate, propagation, timeout, filter, mask);
	open = true;
	implOpen = true;
    }

    private native int openImpl(int portNum) throws IOException;

    private native void configureImpl(int osHandle, int baudrate,
	    int propagation, int timeout, int[] filter, int[] mask)
	    throws IOException;

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
		return new CanInputStream(this);
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
		return new CanOutputStream(this);
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
		int bytesRead = readImpl(osHandle, b, off, len);
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

}
