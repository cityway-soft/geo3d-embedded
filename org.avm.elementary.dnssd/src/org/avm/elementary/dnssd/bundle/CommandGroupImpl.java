package org.avm.elementary.dnssd.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.dnssd.DnsSdServiceConfig;
import org.avm.elementary.dnssd.DnsSdServiceImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "dnssd";

	private DnsSdServiceImpl _peer;

	CommandGroupImpl(ComponentContext context, DnsSdServiceImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the dnssd service.");
		_peer = peer;
	}

	// PORT
	public final static String USAGE_SETPORT = "<port>";

	public final static String[] HELP_SETPORT = new String[] { "Set PORT", };

	public int cmdSetport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer port = new Integer((String) opts.get("port"));
		((DnsSdServiceConfig) _config).setPort(port);
		_config.updateConfig();

		out.println("Current  PORT : "
				+ ((DnsSdServiceConfig) _config).getPort());
		return 0;
	}

	public final static String USAGE_SHOWPORT = "";

	public final static String[] HELP_SHOWPORT = new String[] { "Show current  PORT", };

	public int cmdShowport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current  PORT : "
				+ ((DnsSdServiceConfig) _config).getPort());
		return 0;
	}

	// ADDRESS
	public final static String USAGE_SETADDRESS = "<address>";

	public final static String[] HELP_SETADDRESS = new String[] { "Set  ADDRESS", };

	public int cmdSetaddress(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String address = ((String) opts.get("address")).trim();
		((DnsSdServiceConfig) _config).setAddress(address);
		_config.updateConfig();

		out.println("Current  ADDRESS : "
				+ ((DnsSdServiceConfig) _config).getAddress());
		return 0;
	}

	public final static String USAGE_SHOWADDRESS = "";

	public final static String[] HELP_SHOWADDRESS = new String[] { "Show current  ADDRESS", };

	public int cmdShowaddress(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current  ADDRESS : "
				+ ((DnsSdServiceConfig) _config).getAddress());
		return 0;
	}

	// _PERIOD
	public final static String USAGE_SETPERIOD = "<period>";

	public final static String[] HELP_SETPERIOD = new String[] { "Set ping period en (s)", };

	public int cmdSetperiod(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer period = new Integer(((String) opts.get("period")).trim());
		((DnsSdServiceConfig) _config).setPeriod(period);
		_config.updateConfig();

		out.println("Current ping period : "
				+ ((DnsSdServiceConfig) _config).getPeriod());
		return 0;
	}

	public final static String USAGE_SHOWPERIOD = "";

	public final static String[] HELP_SHOWPERIOD = new String[] { "Show ping period", };

	public int cmdShowperiod(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current ping period : "
				+ ((DnsSdServiceConfig) _config).getPeriod());
		return 0;
	}

	public final static String USAGE_SHOWMODEL = "";

	public final static String[] HELP_SHOWMODEL = new String[] { "Show current MODEL", };

	public int cmdShowmodel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current STATE : " + _peer.toString());
		return 0;
	}

}
