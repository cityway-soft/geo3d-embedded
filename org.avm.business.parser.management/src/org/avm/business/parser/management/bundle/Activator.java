package org.avm.business.parser.management.bundle;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.avm.business.parser.management.ParserImpl;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.parser.Parser;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator implements Parser {

	static final String PID = ParserImpl.class.getName();

	private Logger _log;

	private ComponentContext _context;


	private ParserImpl _peer;


	public Activator() {
		_log = Logger.getInstance(this.getClass().getName());
	}

	private void initializeParser() {
		BundleContext bc = _context.getBundleContext();
		Bundle b = bc.getBundle();
		Enumeration it = b.findEntries("/", "management.jar", true);
		URL url = null;
		while (it.hasMoreElements()) {
			url = (URL) it.nextElement();
		}
		_peer = new ParserImpl(url);

	}

	protected void activate(ComponentContext context) {
		_context = context;
		initializeParser();
	}

	protected void deactivate(ComponentContext context) {

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
