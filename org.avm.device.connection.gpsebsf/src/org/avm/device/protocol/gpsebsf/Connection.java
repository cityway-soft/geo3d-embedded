package org.avm.device.protocol.gpsebsf;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.StringTokenizer;

public class Connection extends URLConnection implements Runnable {

	private boolean open;
	private MulticastSocket socket;
	private InetAddress multicastAdressGroup;
	private PipedOutputStream out;
	private PipedInputStream in;
	private boolean running;
	private Thread thread;

	public Connection(URL url) {
		super(url);
		open = false;
		out = new PipedOutputStream();

	}

	public InputStream getInputStream() throws IOException {
		//System.out.println("[DLA] Connection GPS EBSF..........................");
		if (in == null) {
			in = new PipedInputStream(out);
			connect();
		}
		return in;
	}

	public byte[] format(byte[] data) {
		String xml = new String(data);
		byte[] frame;
		if (xml.indexOf("<NMEA") != -1) {
			//System.out.println("[DLA] EBSF XML data (" + data.length + ") : " + xml);
			int idx = xml.indexOf("<NMEA>");
			String temp = xml.substring(idx);
			idx = temp.indexOf("</NMEA>");
			String nmea = temp.substring("<NMEA>".length(), idx)+"\n";
			frame = nmea.getBytes();
		} else {
			frame = data;
		}
		return frame;
	}

	public void connect() throws IOException {
		open(url.getPort(), url.getHost(), url.getQuery());
		
		running=true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void disconnect() throws IOException {
		if (thread != null){
			running=false;
			thread.interrupt();
			thread=null;
		}
		close();
	}
	

	protected void open(int port, String group, String query) throws IOException {
		if (!open) {
			socket = new MulticastSocket(port);
			multicastAdressGroup = InetAddress.getByName(group);
			socket.joinGroup(multicastAdressGroup);

			open=true;
		}
	}

	protected void close() throws IOException {
		if (open) {
			socket.leaveGroup(multicastAdressGroup);
			socket.close();
			open = false;
		}
		
	}

	public void run() {
		byte[] buf = new byte[256];

		while (running) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					socket.receive(packet);
					byte[] data = format(packet.getData());
					//System.out.println("[DLA] nmea frame to write :" + new String(data));
					out.write(data);
				} catch (IOException e) {
					try {
						close();
						open(url.getPort(), url.getHost(), url.getQuery());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			

		}
	}

}
