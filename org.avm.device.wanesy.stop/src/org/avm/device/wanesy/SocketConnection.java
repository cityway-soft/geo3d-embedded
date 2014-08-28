package org.avm.device.wanesy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;

/**
 * 
 * @author flabourot
 * 
 */

public abstract class SocketConnection extends Thread {
	private String address;
	private int port;
	private int timeBetweenConnectionRetry = 1000;
	private Object lock = new Object();

	private Socket socket = null;
	private InputStream socketIs = null;
	private OutputStream socketOs = null;

	public SocketConnection(String address, int port) {
		this.address = address;
		this.port = port;
	}

	public boolean openConnection() {
		if (socket != null) {
			closeConnection();
		}
		try {
			socket = new Socket(address, port);
		} catch (UnknownHostException e) {
			socket = null;
			return false;
		} catch (IOException e) {
			socket = null;
			return false;
		}
		return true;
	}

	private void closeConnection() {

	}

	private void unWait() {
		synchronized (lock) {
			lock.notify();
		}
	}

	// return true if mili is reached
	private boolean waitOrQuit(int mili) {
		synchronized (lock) {
			try {
				lock.wait(mili);
			} catch (InterruptedException e) {
				return false;
			}
		}
		return true;
	}

	public boolean isConnected() {
		return (socket != null);
	}

	public boolean send(String data) {
		boolean ret = false;
		if (socketOs != null) {
			try {
				System.out.println(data.getBytes());
				socketOs.write(data.getBytes());
				socketOs.flush();
				ret = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	public void run() {
		System.out.println("MAINNNNNNNNNNNN THREADDDDDDDDDDDDDDD");
		// try to connect
		while (openConnection() == false) {
			System.out.println("wait before retry ...");
			if (!waitOrQuit(timeBetweenConnectionRetry)) {
				break;
			}
		}
		if (socket != null) {
			try {
				socketOs = socket.getOutputStream();
				socketIs = socket.getInputStream();
				byte buffer[] = new byte[4096];
				int count = 0;
				// while ((count = buffer, count2, buffer.length
				// - count2)) >= 0) {
				onInit ();
				while ((count = socketIs.read(buffer)) >= 0) {
					System.out.println(count);
					displayBuffer(buffer, count);
					onRead(buffer, count);
				}

			} catch (IOException e) {
				//
			}
		}
	}

	protected void displayBuffer(byte buffer[], int count) {
		StringBuffer data = new StringBuffer();
		for (int i = 0; i < count; ++i) {
			// System.out.println(buffer[i]);
			data.append((char) buffer[i]);
		}
		System.out.println(data.toString());
		// Kms result = KmsFactory.unmarshal(data.toString());
	}

	public OutputStream getOuputStream() {
		return socketOs;
	}

	public abstract void onRead(byte[] buffer, int count);
	public abstract void onInit();

}
