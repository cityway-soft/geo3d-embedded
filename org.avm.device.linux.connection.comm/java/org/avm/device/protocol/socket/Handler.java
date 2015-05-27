package org.avm.device.protocol.socket;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {

	protected URLConnection openConnection(URL url) {
		return new SocketConnection(url);
	}
}

