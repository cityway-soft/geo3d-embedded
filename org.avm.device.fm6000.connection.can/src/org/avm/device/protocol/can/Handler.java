package org.avm.device.protocol.can;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {

	public URLConnection openConnection(URL url) {
		return new UrlConnection(url);
	}
}
