package org.avm.business.girouette.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.business.girouette.Girouette;
import org.avm.business.girouette.GirouetteImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String COMMAND_GROUP = "client.girouette";

	private ConfigImpl _config;

	private Girouette _peer;

	CommandGroupImpl(ComponentContext context, Girouette peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for 'client girouette'.");
		_peer = peer;
		_config = config;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all properties", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Properties properties = null;
		;// a refaire
		if (properties != null) {
			Enumeration en = properties.keys();
			StringBuffer buf = new StringBuffer();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String value = properties.getProperty(key);
				buf.append(key);
				buf.append("=");
				buf.append(value);
				buf.append(System.getProperty("line.separator"));
			}
			out.print(buf.toString());
		} else {
			out.println("no specific code.");
		}
		return 0;
	}

	// Send
	public final static String USAGE_SEND = "<codetag>";

	public final static String[] HELP_SEND = new String[] { "Send code tag to girouette", };

	public int cmdSend(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String codetag = ((String) opts.get("codetag")).trim();
		int code;
		code = ((GirouetteImpl) _peer).getSpecialCode(codetag);
		if (code > 0) {
			out.println(codetag + " => " + code);
			((GirouetteImpl) _peer).destination(code);
			out.println("send.");
		} else {
			out.println("Error: no girouette code for '" + codetag + "'");
		}

		return 0;
	}

}
