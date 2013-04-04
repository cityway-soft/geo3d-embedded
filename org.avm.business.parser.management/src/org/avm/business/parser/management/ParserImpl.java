package org.avm.business.parser.management;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;
import org.avm.business.protocol.management.Management;
import org.avm.business.protocol.management.MessageFactory;
import org.avm.elementary.parser.AbstractParser;

public class ParserImpl extends AbstractParser {

	protected ClassLoader _loader;

	public ParserImpl(URL url) {
		_log = Logger.getInstance(this.getClass());
		URLConnection conn;
		try {
			_log.debug("URL:" + url);
			conn = url.openConnection();
			initialize(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initialize(URLConnection conn) throws Exception {
		_loader = Thread.currentThread().getContextClassLoader();
		JarInputStream zip = new JarInputStream(conn.getInputStream());
		JarEntry entry;
		_log.debug("while zip entry exists....");
		while ((entry = (JarEntry) zip.getNextEntry()) != null) {
			String name = entry.getName();
			_log.debug("  zip entry name:" + name);
			if (name.endsWith(".class")) {
				String classname = name.replace('/', '.');
				classname = classname.substring(0, classname.length() - 6);
				Class clazz;
				try {
					clazz = Class.forName(classname);
					if (_loader == null) {
						_loader = clazz.getClassLoader();
					}
					_log.debug(clazz.getName() + " loaded loader : "
							+ clazz.getClassLoader());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		_log.debug("end zip");
	}

	public String getProtocolName() {
		return MessageFactory.PROTOCOL_NAME;
	}

	public int getProtocolId() {
		return MessageFactory.PROTOCOL_ID;
	}

	public void marshal(Object n, OutputStream out) throws Exception {
		if (n instanceof Management) {
			Management message = (Management) n;
			message.marshal(out);
		} else {
			throw new RuntimeException("Protocole non supporte");
		}
	}

	public Object unmarshal(InputStream in) throws Exception {
		Management message = MessageFactory.unmarshal(in);
		return message;

	}

	public Object get(InputStream in) throws Exception {
		in.reset();
		Management message = MessageFactory.get(in);
		return message;
	}

	public void put(Object n, OutputStream out) throws Exception {
		if (n instanceof Management) {
			Management message = (Management) n;
			message.put(out);
		} else {
			throw new RuntimeException("Protocole non supporte");
		}
	}

}
