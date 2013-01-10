package org.avm.device.protocol.canebsf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPClient implements Client {

	private DatagramSocket socket = null;
	private InetAddress address;
	private int buffersize;
	private DatagramPacket packet=null;

	public void open(int port, String addr, int buffersize) throws IOException {
		socket = new DatagramSocket();
		address = InetAddress.getByName(addr);
		this.buffersize = buffersize;

		// --send request
		byte[] buf = "FEF1".getBytes();
		packet = new DatagramPacket(buf, buf.length, address, port);
		System.out.println("Send packet: " + packet);
		socket.send(packet);
		System.out.println("Send packet: ok");

	}

	public void close() throws IOException {
		socket.close();
	}

	public byte[] receive() throws Exception {
		byte[] buf = new byte[buffersize];
		//-- Set a receive timeout, 2000 milliseconds
		//socket.setSoTimeout(2000);

		//-- Prepare the packet for receive
		packet.setData(buf);
		socket.receive(packet);
		return packet.getData();
	}

	private void debug(String debug) {
		System.out.println("[DLA] - CanEbsf UDPClient : " + debug);
	}

	public int getBufferSize() {
		return buffersize;
	}
}
