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

import org.avm.device.protocol.canebsf.Connection.ConnectionInputStream;

public class Connection extends URLConnection {
	private static final int BUFFER_SIZE = 256;

	private boolean open;
	private Client client;

	private Connection instance;

	int buffersize = BUFFER_SIZE;

	private ConnectionInputStream in;

	private boolean debug;

	public InputStream getInputStream() throws IOException {
		return in;
	}

	public Connection(URL url) {
		super(url);
		instance = this;
		in = new ConnectionInputStream();
		open = false;
	}

	public Object getContent() throws java.io.IOException {
		Object result = null;
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

		try {
			result = format(client.receive());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public byte[] format(byte[] data) {
		byte[] pgn;
		String xml = new String(data);
		debug("DATA (" + xml.length() + "): [" + xml + "]");
		if (xml.toUpperCase().indexOf("<PGN>") != -1) {
			int idx = xml.indexOf("<PGN>");
			String temp = xml.substring(idx);
			idx = temp.indexOf("</PGN>");
			String pgnid = temp.substring("<PGN>".length(), idx);
			debug("PGNID=" + pgnid);

			idx = temp.indexOf("<data>");
			temp = temp.substring(idx);
			idx = temp.indexOf("</data>");
			String pgndata = temp.substring("<data>".length(), idx);
			debug("PGNDATA=" + pgndata);

			StringBuffer frame = new StringBuffer();
			frame.append("00");
			frame.append(pgnid.substring(2, 4));
			frame.append(pgnid.substring(0, 2));
			frame.append("00");
			frame.append("02");
			frame.append("00");
			frame.append(pgndata);
			debug("FRAME(" + frame.length() / 2 + ")" + frame);
			pgn = fromHexaString(frame.toString());
			debug("pgn size:" + pgn.length);
		} else {
			debug("[DLA] CAN data (" + data.length + ")");
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
			query = temp.substring(idx + 1);
		}
		idx = temp.indexOf("&");
		if (idx != -1) {
			port = temp.substring(0, idx);
			query = temp.substring(idx + 1);
		}

		System.out.println("[DLA] host: " + host + ", port:" + port + ",query="
				+ query);

		open(Integer.parseInt(port), host, query);
	}

	protected void open(int port, String address, String query)
			throws IOException {
		if (!open) {
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
				debug("Query props = " + props.toString());
				String sdebug = props.getProperty("debug");
				debug = (sdebug != null) ? sdebug.toLowerCase().equals("true")
						: false;
				String ssize = props.getProperty("buffersize");
				if (ssize != null) {
					buffersize = Integer.parseInt(ssize);
					if (buffersize == 0) {
						buffersize = BUFFER_SIZE;
					}
				}
				debug("buffersize=" + buffersize);// + ", multicast=" +
													// multicast);
			}

			InetAddress addr = InetAddress.getByName(address);
			if (addr.isMulticastAddress()) {
				client = new MulticastClient();
				debug("multicast client");
			} else {
				client = new BroadcastClient();
				debug("broadcast client");
			}

			client.open(port, address, buffersize);

			open = true;
		}
	}

	public void close() throws IOException {
		if (open) {
			debug("****************************************CLOSE*************************************************");
			client.close();
			open = false;
		}
	}

	private void debug(String debug) {
		if (this.debug) {
			System.out.println("[DLA] - CanEbsf Connection : " + debug);
		}
	}

	/*
	 * classe bidon pour gérer la cloture de connection En effet, le client
	 * appelle getInputStream().close()
	 */
	class ConnectionInputStream extends InputStream {
		public int read() throws IOException {
			return 0;
		}

		public void close() throws IOException {
			instance.close();
		}

	}

}
