package org.avm.elementary.media.jms.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.media.jms.MediaJMSConfig;
import org.avm.elementary.media.jms.MediaJMSImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "jms";

	private MediaJMSImpl _peer;

	CommandGroupImpl(ComponentContext context, MediaJMSImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the jms media.");
		_peer = peer;
	}

	// log level org.jboss
	public final static String USAGE_SETPROVIDERLEVEL = "<level>";

	public final static String[] HELP_SETPROVIDERLEVEL = new String[] { "Set log level", };

	public int cmdSetproviderlevel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String level = ((String) opts.get("level")).trim();
		Logger _log = Logger.getInstance("org.jboss");
		_log.setPriority(Priority.toPriority(level, Priority.DEBUG));
		out.println("Current log level : " + _log.getPriority());
		return 0;
	}

	public final static String USAGE_SHOWPROVIDERLEVEL = "";

	public final static String[] HELP_SHOWPROVIDERLEVEL = new String[] { "Show current log level." };

	public int cmdShowproviderlevel(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		Logger _log = Logger.getInstance("org.jboss");
		out.println("Current log level : " + _log.getPriority());
		return 0;
	}

	// MEDIA ID
	/*
	 * public final static String USAGE_SETMEDIAID = "<id>";
	 * 
	 * public final static String[] HELP_SETMEDIAID = new String[] { "Set MEDIA
	 * ID", };
	 * 
	 * public int cmdSetmediaid(Dictionary opts, Reader in, PrintWriter out,
	 * Session session) { String id = ((String) opts.get("id")).trim();
	 * 
	 * _config.updateConfig();
	 * 
	 * out.println("Current MEDIA ID : " + ((MediaJMSConfig)
	 * _config).getMediaId()); return 0; }
	 * 
	 */
	public final static String USAGE_SHOWMEDIAID = "";

	public final static String[] HELP_SHOWMEDIAID = new String[] { "Show current MEDIA ID", };

	public int cmdShowmediaid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current MEDIA ID : "
				+ ((MediaJMSConfig) _config).getMediaId());
		return 0;
	}

	// UIL PORT
	public final static String USAGE_SETUILPORT = "<port>";

	public final static String[] HELP_SETUILPORT = new String[] { "Set UIL PORT", };

	public int cmdSetuilport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer port = new Integer((String) opts.get("port"));
		((MediaJMSConfig) _config).setUilPort(port);
		_config.updateConfig();

		out.println("Current UIL PORT : "
				+ ((MediaJMSConfig) _config).getUilPort());
		return 0;
	}

	public final static String USAGE_SHOWUILPORT = "";

	public final static String[] HELP_SHOWUILPORT = new String[] { "Show current UIL PORT", };

	public int cmdShowuilport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current UIL PORT : "
				+ ((MediaJMSConfig) _config).getUilPort());
		return 0;
	}

	// UIL ADDRESS
	public final static String USAGE_SETUILADDRESS = "<address>";

	public final static String[] HELP_SETUILADDRESS = new String[] { "Set UIL ADDRESS", };

	public int cmdSetuiladdress(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String address = ((String) opts.get("address")).trim();
		((MediaJMSConfig) _config).setUilAddress(address);
		_config.updateConfig();

		out.println("Current UIL ADDRESS : "
				+ ((MediaJMSConfig) _config).getUilAddress());
		return 0;
	}

	public final static String USAGE_SHOWUILADDRESS = "";

	public final static String[] HELP_SHOWUILADDRESS = new String[] { "Show current UIL ADDRESS", };

	public int cmdShowuiladdress(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current UIL ADDRESS : "
				+ ((MediaJMSConfig) _config).getUilAddress());
		return 0;
	}

	// SERVER_IL_FACTORY
	public final static String USAGE_SETSERVERILFACTORY = "<factory>";

	public final static String[] HELP_SETSERVERILFACTORY = new String[] { "Set server il factory", };

	public int cmdSetserverilfactory(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String factory = ((String) opts.get("factory")).trim();
		((MediaJMSConfig) _config).setServerILFactory(factory);
		_config.updateConfig();

		out.println("Current server il factory : "
				+ ((MediaJMSConfig) _config).getUilAddress());
		return 0;
	}

	public final static String USAGE_SHOWSERVERILFACTORY = "";

	public final static String[] HELP_SHOWSERVERILFACTORY = new String[] { "Show server il factory", };

	public int cmdShowserverilfactory(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current server il factory : "
				+ ((MediaJMSConfig) _config).getServerILFactory());
		return 0;
	}

	// CLIENT_IL_SERVICE
	public final static String USAGE_SETCLIENTILSERVICE = "<service>";

	public final static String[] HELP_SETCLIENTILSERVICE = new String[] { "Set client il service", };

	public int cmdSetclientilservice(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String service = ((String) opts.get("service")).trim();
		((MediaJMSConfig) _config).setClientILService(service);
		_config.updateConfig();

		out.println("Current client il service : "
				+ ((MediaJMSConfig) _config).getClientILService());
		return 0;
	}

	public final static String USAGE_SHOWCLIENTILSERVICE = "";

	public final static String[] HELP_SHOWCLIENTILSERVICE = new String[] { "Show client il service", };

	public int cmdShowclientilservice(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current client il service : "
				+ ((MediaJMSConfig) _config).getClientILService());
		return 0;
	}

	// DESTINATION
	public final static String USAGE_SETDESTINATION = "<destination>";

	public final static String[] HELP_SETDESTINATIONE = new String[] { "Set destination", };

	public int cmdSetdestination(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String destination = ((String) opts.get("destination")).trim();
		((MediaJMSConfig) _config).setDestination(destination);
		_config.updateConfig();

		out.println("Current destination : "
				+ ((MediaJMSConfig) _config).getDestination());
		return 0;
	}

	public final static String USAGE_SHOWDESTINATION = "";

	public final static String[] HELP_SHOWDESTINATION = new String[] { "Show destination", };

	public int cmdShowdestination(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current destination : "
				+ ((MediaJMSConfig) _config).getDestination());
		return 0;
	}

	// PING_PERIOD
	public final static String USAGE_SETPINGPERIOD = "<period>";

	public final static String[] HELP_SETPINGPERIOD = new String[] { "Set ping period", };

	public int cmdSetpingperiod(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer period = new Integer(((String) opts.get("period")).trim());
		((MediaJMSConfig) _config).setPingPeriod(period);
		_config.updateConfig();

		out.println("Current ping period : "
				+ ((MediaJMSConfig) _config).getPingPeriod());
		return 0;
	}

	public final static String USAGE_SHOWPINGPERIOD = "";

	public final static String[] HELP_SHOWPINGPERIOD = new String[] { "Show ping period", };

	public int cmdShowpingperiodn(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current ping period : "
				+ ((MediaJMSConfig) _config).getPingPeriod());
		return 0;
	}
}
