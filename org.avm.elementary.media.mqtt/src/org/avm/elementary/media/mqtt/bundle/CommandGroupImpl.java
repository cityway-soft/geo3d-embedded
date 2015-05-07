package org.avm.elementary.media.mqtt.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.media.mqtt.MediaMqttConfig;
import org.avm.elementary.media.mqtt.MediaMqttImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "media.mqtt";

	private MediaMqttImpl _peer;

	CommandGroupImpl(ComponentContext context, MediaMqttImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the mqtt media.");
		_peer = peer;
	}

	// MEDIA ID

	public final static String USAGE_SHOWMEDIAID = "";

	public final static String[] HELP_SHOWMEDIAID = new String[] { "Show current MEDIA ID", };

	public int cmdShowmediaid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current MEDIA ID : "
				+ ((MediaMqttConfig) _config).getMediaId());
		return 0;
	}

	// PORT
	public final static String USAGE_SETPORT = "<port>";

	public final static String[] HELP_SETPORT = new String[] { "Set PORT", };

	public int cmdSetport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer port = new Integer((String) opts.get("port"));
		((MediaMqttConfig) _config).setPort(port);
		_config.updateConfig();

		out.println("Current  PORT : " + ((MediaMqttConfig) _config).getPort());
		return 0;
	}

	public final static String USAGE_SHOWPORT = "";

	public final static String[] HELP_SHOWPORT = new String[] { "Show current  PORT", };

	public int cmdShowport(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current  PORT : " + ((MediaMqttConfig) _config).getPort());
		return 0;
	}

	// ADDRESS
	public final static String USAGE_SETADDRESS = "<address>";

	public final static String[] HELP_SETADDRESS = new String[] { "Set  ADDRESS", };

	public int cmdSetaddress(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String address = ((String) opts.get("address")).trim();
		((MediaMqttConfig) _config).setAddress(address);
		_config.updateConfig();

		out.println("Current  ADDRESS : "
				+ ((MediaMqttConfig) _config).getAddress());
		return 0;
	}

	public final static String USAGE_SHOWADDRESS = "";

	public final static String[] HELP_SHOWADDRESS = new String[] { "Show current  ADDRESS", };

	public int cmdShowaddress(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current  ADDRESS : "
				+ ((MediaMqttConfig) _config).getAddress());
		return 0;
	}

	// _PERIOD
	public final static String USAGE_SETPERIOD = "<period>";

	public final static String[] HELP_SETPERIOD = new String[] { "Set ping period en (s)", };

	public int cmdSetperiod(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Integer period = new Integer(((String) opts.get("period")).trim());
		((MediaMqttConfig) _config).setPeriod(period);
		_config.updateConfig();

		out.println("Current ping period : "
				+ ((MediaMqttConfig) _config).getPeriod());
		return 0;
	}

	public final static String USAGE_SHOWPERIOD = "";

	public final static String[] HELP_SHOWPERIOD = new String[] { "Show ping period", };

	public int cmdShowperiod(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current ping period : "
				+ ((MediaMqttConfig) _config).getPeriod());
		return 0;
	}

	public final static String USAGE_SHOWMODEL = "";

	public final static String[] HELP_SHOWMODEL = new String[] { "Show current MODEL", };

	public int cmdShowmodel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current STATE : " + _peer.toString());
		return 0;
	}

	public final static String USAGE_ISCONNECTED = "";

	public final static String[] HELP_ISCONNECTED = new String[] { "Test if connection is up", };

	public int cmdIsconnected(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		if (_peer == null){
			out.println("Peer MediaCTW is null !");
			return 1;
		}
		int failure = ((MediaMqttImpl) _peer).getFailureCount();
		Date date = ((MediaMqttImpl) _peer).getConnectionDate();
		if (failure != 0 || date == null) {
			out.println("NOT CONNECTED (#" + failure + " failures)");
		} else {
			long delta = (System.currentTimeMillis() - date.getTime()) / 1000;
			out.println("CONNECTED (since " + date + ", " + delta + " sec)");

		}

		return 0;
	}
}
