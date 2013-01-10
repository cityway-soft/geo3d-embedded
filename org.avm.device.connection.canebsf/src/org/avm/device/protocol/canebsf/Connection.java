package org.avm.device.protocol.canebsf;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.StringTokenizer;

public class Connection extends URLConnection {
	private static final int BUFFER_SIZE = 256;

	private boolean open;
	private MulticastSocket socket;
	private InetAddress multicastAdressGroup;

	private int buffersize = BUFFER_SIZE;

	public InputStream getInputStream() throws IOException {
		return null;
	}

	public Connection(URL url) {
		super(url);
		open = false;
	}

	public Object getContent() throws java.io.IOException {
		Object result = null;
		byte[] buf = new byte[buffersize];
		if (!open) {

			try {
				connect();
			} catch (IOException e) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
				throw e;
			}
		}
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);

		result = format(packet.getData());

		return result;
	}

	public byte[] format(byte[] data) {
		byte[] pgn;
		String xml = new String(data);
		if (xml.toLowerCase().indexOf("<?xml") != -1) {
			// System.out.println("[DLA] EBSF XML data ("+data.length+")");

			int idx = xml.indexOf("<PGN>");
			String temp = xml.substring(idx);
			idx = temp.indexOf("</PGN>");
			String pgnid = temp.substring("<PGN>".length(), idx);
			// System.out.println("PGNID=" + pgnid);

			idx = temp.indexOf("<data>");
			temp = temp.substring(idx);
			idx = temp.indexOf("</data>");
			String pgndata = temp.substring("<data>".length(), idx);
			// System.out.println("PGNDATA=" + pgndata);

			StringBuffer frame = new StringBuffer();
			frame.append("00");
			frame.append(pgnid.substring(2, 4));
			frame.append(pgnid.substring(0, 2));
			frame.append("00");
			frame.append(pgndata);
			// System.out.println("FRAME(" + frame.length() / 2 + ")" + frame);
			pgn = fromHexaString(frame.toString());
			// System.out.println("pgn size:(" + pgn.length);
		} else {
			// System.out.println("[DLA] CAN data ("+data.length+")");
			pgn = new byte[12];
			System.arraycopy(data, 0, pgn, 0, pgn.length);
		}

		return pgn;
	}

	public static byte[] fromHexaString(String hexaString) {
		byte[] buffer = hexaString.getBytes();
		byte[] data = new byte[buffer.length / 2];
		for (int i = 0; i < data.length; i++) {
			int index = i * 2;
			int rValue = (buffer[i * 2] > 0x39) ? buffer[index] - 0x37
					: buffer[index] - 0x30;
			int lValue = (buffer[i * 2 + 1] > 0x39) ? buffer[index + 1] - 0x37
					: buffer[index + 1] - 0x30;
			data[i] = (byte) (((rValue << 4) & 0xF0) | (lValue & 0x0F));

		}
		return data;
	}

	public void connect() throws IOException {
		String temp = url.toExternalForm();
		// System.out.println("[DLA] temp: " + temp);

		int idx = temp.indexOf(":");

		temp = temp.substring(idx + 1);
		// System.out.println("[DLA] temp2: " + temp);

		idx = temp.indexOf(":");
		// System.out.println("[DLA] idx: " + idx);

		String host = temp.substring(0, idx);
		// System.out.println("[DLA] host: " + host);

		temp = temp.substring(idx + 1);
		String query = null;
		String port = temp;
		idx = temp.indexOf(";");
		if (idx != -1) {
			port = temp.substring(0, idx);
			query = temp.substring(idx+1);
		}
		idx = temp.indexOf("&");
		if (idx != -1) {
			port = temp.substring(0, idx);
			query = temp.substring(idx+1);
		}

		System.out.println("[DLA] host: " + host + ", port:" + port + ",query="+query);

		open(Integer.parseInt(port), host, query);
	}

	protected void open(int port, String group, String query)
			throws IOException {
		if (!open) {
			socket = new MulticastSocket(port);
			multicastAdressGroup = InetAddress.getByName(group);
			socket.joinGroup(multicastAdressGroup);
			if (query != null) {
				String temp = query.replace(';', '&');
				temp = temp.replace('?', '&');
				StringTokenizer t = new StringTokenizer(temp, "&");
				Properties props = new Properties();
				while (t.hasMoreElements()) {
					String sprop = (String) t.nextElement();
					StringTokenizer t2 = new StringTokenizer(sprop, "=");
					String key = t2.nextToken().trim();
					String value = "";
					if (t2.hasMoreElements()) {
						value = t2.nextToken().trim();
					}
					props.put(key, value);
				}
				String ssize = props.getProperty("buffersize");
				if (ssize != null) {
					buffersize = Integer.parseInt(ssize);
				}
			}
			open = true;
		}
	}

	public void close() throws IOException {
		if (open) {
			socket.leaveGroup(multicastAdressGroup);
			socket.close();
			open = false;
		}
	}

}
