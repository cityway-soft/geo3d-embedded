package org.avm.business.parser.phoebus.bundle;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import org.avm.business.parser.phoebus.ParserImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.parser.Parser;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Parser {

	static final String PID = ParserImpl.class.getName();

	private ParserImpl _peer;

	public static ClassLoader _bundleClassLoader;

	public Activator() {

	}

	protected void start(ComponentContext context) {
		initializeParser();

	}

	protected void stop(ComponentContext context) {
	}

	private void initializeParser() {
		BundleContext bc = _context.getBundleContext();
		_bundleClassLoader = bc.getClass().getClassLoader();
		bc.getClass().getClassLoader();
		Bundle b = bc.getBundle();
		Enumeration it = b.findEntries("/", "phoebus.jar", true);
		URL url = null;
		while (it.hasMoreElements()) {
			url = (URL) it.nextElement();
		}
		_peer = new ParserImpl(url);

	}

	public String getProtocolName() {
		if (_peer == null)
			return null;
		return _peer.getProtocolName();
	}

	public int getProtocolId() {
		if (_peer == null)
			return -1;
		return _peer.getProtocolId();
	}

	public Object get(InputStream in) throws Exception {
		if (_peer == null)
			throw new Exception();
		return _peer.get(in);
	}

	public void put(Object n, OutputStream out) throws Exception {
		if (_peer == null)
			throw new Exception();
		_peer.put(n, out);
	}

	public void marshal(Object n, OutputStream out) throws Exception {
		if (_peer == null)
			throw new Exception();
		_peer.marshal(n, out);

	}

	public Object unmarshal(InputStream in) throws Exception {
		if (_peer == null)
			throw new Exception();
		return _peer.unmarshal(in);
	}
}
