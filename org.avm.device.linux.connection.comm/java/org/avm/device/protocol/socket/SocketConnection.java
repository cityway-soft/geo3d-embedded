package org.avm.device.protocol.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class SocketConnection extends URLConnection {

	private String SOCKET_SCHEME = "socket://";
	private Socket socket = null;

	protected SocketConnection(URL arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void connect() throws IOException {
		// socket://ip:port
		String surl = url.toExternalForm().trim();
		if (!surl.startsWith(SOCKET_SCHEME)) {
			throw new IOException("Invalid URL");
		}
		int i = surl.indexOf(":", SOCKET_SCHEME.length() + 1);
		String host = surl.substring(SOCKET_SCHEME.length(), i);
		String port = surl.substring(i + 1);
		int p=0;
		try {
		 p = Integer.parseInt(port);
		} catch (NumberFormatException nfe) {
			throw new IOException("Invalid Port");
		}
		socket = new Socket(host, p);
	}

	public OutputStream getOutputStream() throws IOException {
		if (socket == null){
			connect();
		}
		return socket.getOutputStream();
	}
	
	public InputStream getInputStream() throws IOException {
		if (socket == null){
			connect();
		}
		return socket.getInputStream();
	}

	
	/*public static void main(String[] args) {
		try {
			SocketConnection s = new SocketConnection(new URL(
					"socket://192.168.1.1:5555"));
			s.connect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
