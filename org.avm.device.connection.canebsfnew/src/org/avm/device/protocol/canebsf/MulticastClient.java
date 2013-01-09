package org.avm.device.protocol.canebsf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient implements Client {

	private MulticastSocket socket;
	private InetAddress multicastAdressGroup;
	private int buffersize;

	public void open(int port, String addgroup, int buffersize)
			throws IOException {
		socket = new MulticastSocket(port);
		multicastAdressGroup = InetAddress.getByName(addgroup);
		socket.joinGroup(multicastAdressGroup);
		this.buffersize = buffersize;

	}

	public void close() throws IOException {
		socket.leaveGroup(multicastAdressGroup);
		socket.close();
	}

	public byte[] receive() throws Exception {
		byte[] buf = new byte[buffersize];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		//debug("waiting for multicast packets...");
		socket.receive(packet);
		return packet.getData();
	}

//	private void debug(String debug) {
//		System.out.println("[DLA] - CanEbsf MulticastClient : " + debug);
//	}

	public int getBufferSize() {
		return buffersize;
	}
}
