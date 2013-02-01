/*
 * Created on 13 mai 2005 To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.avm.business.parser.phoebus;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.Message;
import org.avm.business.protocol.phoebus.MessageFactory;
import org.avm.elementary.parser.AbstractParser;

public class ParserImpl extends AbstractParser {

	protected ClassLoader _loader;

	public ParserImpl(URL url) {
		_log = Logger.getInstance(this.getClass());
		URLConnection conn;
		try {
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
		while ((entry = (JarEntry) zip.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.endsWith(".class")) {
				String classname = name.replace('/', '.');
				classname = classname.substring(0, classname.length() - 6);
				Class clazz;
				try {
					clazz = Class.forName(classname);
					if (_loader == null) {
						_loader = clazz.getClassLoader();
					}
					// System.out.println(clazz.getName() + " loaded loader : "
					// + clazz.getClassLoader());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getProtocolName() {
		return Entete.PROTOCOL_NAME;
	}

	public int getProtocolId() {
		return Entete.PROTOCOL_ID;
	}

	public void marshal(Object n, OutputStream out) throws Exception {
		if (n instanceof Message) {
			Message message = (Message) n;
			message.marshal(out);
		} else {
			throw new RuntimeException("Protocole non supporte");
		}
	}

	public Object unmarshal(InputStream in) throws Exception {
		Message message = MessageFactory.parseXmlstream(in);
		return message;

	}

	public Object get(InputStream in) throws Exception {
		Message message = MessageFactory.parseBitstream(in);
		return message;
	}

	public void put(Object n, OutputStream out) throws Exception {
		if (n instanceof Message) {
			Message message = (Message) n;
			message.put(out);
		} else {
			throw new RuntimeException("Protocole non supporte");
		}
	}

}
