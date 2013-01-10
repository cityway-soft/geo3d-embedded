package org.avm.device.protocol.canebsf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastClient implements Client {

	private DatagramSocket socket = null;
	private InetAddress address;
	private int buffersize;
	private DatagramPacket packet = null;

	public void open(int port, String addr, int buffersize) throws IOException {
		byte[] buf = new byte[buffersize];
		address = InetAddress.getByName(addr);
		socket = new DatagramSocket(port, address);
		packet = new DatagramPacket(buf, buf.length);
		this.buffersize = buffersize;
	}

	public void close() throws IOException {
		socket.close();
	}

	public byte[] receive() throws Exception {
		debug("wait for packet...");
		socket.receive(packet);
		if (packet.getData() != null) {
			debug("receive packet (" + packet.getData().length + ")");
		} else {
			debug("receive null packet!!!!");
		}
		return packet.getData();
	}

	private void debug(String debug) {
		System.out.println("[DLA] - CanEbsf BroadcastClient : " + debug);
	}

	public int getBufferSize() {
		return buffersize;
	}
}
