package org.avm.device.protocol.socket;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.avm.device.protocol.UrlConnection;

public class Handler extends URLStreamHandler {

	public URLConnection openConnection(URL url) {
		return new UrlConnection(url);
	}
}
