package org.avm.device.protocol.rs485;

import java.net.URL;

import java.net.URLStreamHandler;

import org.avm.device.protocol.UrlConnection;

public class Handler extends URLStreamHandler {

	public java.net.URLConnection openConnection(URL url) {
		return new UrlConnection(url);
	}
}
