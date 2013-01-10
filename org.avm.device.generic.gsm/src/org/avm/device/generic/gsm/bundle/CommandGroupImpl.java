package org.avm.device.generic.gsm.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.generic.gsm.GsmConfig;
import org.avm.device.gsm.Constant;
import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmRequest;
import org.avm.device.gsm.GsmResponse;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup implements Constant {

	public static final String COMMAND_GROUP = "gsm";

	private Gsm _peer;

	CommandGroupImpl(ComponentContext context, Gsm peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the gsm.");
		_peer = peer;
		_config = config;
	}

	// URL connection
	public final static String USAGE_SETURLCONNECTION = "<uri>";

	public final static String[] HELP_SETURLCONNECTION = new String[] { "Set URL connection", };

	public int cmdSeturlconnection(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String uri = ((String) opts.get("uri")).trim();
		((GsmConfig) _config).setUrlConnection(uri);
		_config.updateConfig();

		out.println("Current URL connection : "
				+ ((GsmConfig) _config).getUrlConnection());
		return 0;
	}

	public final static String USAGE_SHOWURLCONNECTION = "";

	public final static String[] HELP_SHOWURLCONNECTION = new String[] { "Show current URL connection", };

	public int cmdShowurlconnection(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current URL connection : "
				+ ((GsmConfig) _config).getUrlConnection());
		return 0;
	}

	// AT command
	public final static String USAGE_AT = "[-t #s#] [-g #goodmatch#] <at> [<badmatch>] ...";

	public final static String[] HELP_AT = new String[] { "Send an AT command.\r" };

	public int cmdAt(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String at = ((String) opts.get("at")).trim();
		int to = (opts.get("-t") != null) ? Integer.parseInt(((String) opts
				.get("-t")).trim()) * 1000: 10000;
		String goodMatch = (opts.get("-g") != null) ? ((String) opts.get("-g"))
				.trim() : OK;
		String[] badMatch = (opts.get("badmatch") != null) ? (String[]) opts
				.get("badmatch") : ERROR;

		GsmRequest command = new GsmRequest(at + '\r', goodMatch, badMatch, to);

		try {
			_peer.send(command);
		} catch (Exception e) {
			_log.error(e);
		}

		out.println("AT command : " + command);

		return 0;
	}

	// Check PIN
	public final static String USAGE_SHOWPINSTATUS = "";

	public final static String[] HELP_SHOWPINSTATUS = new String[] { "Show the PIN code status." };

	public int cmdShowpinstatus(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		GsmRequest command = new GsmRequest(AT_CHECK_PIN, CPIN_READY,
				Constant.ERROR, 0);
		try {
			_peer.send(command);
		} catch (Exception e) {
			_log.error(e);
		}

		out.println("AT command : " + command);
		return 0;

	}

	public final static String USAGE_SHOWQUALITY = "";

	public final static String[] HELP_SHOWQUALITY = new String[] { "Show the signal quality." };

	public int cmdShowquality(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			final String[] QUALITY = new String[] { "UNKNOWN  ", "VERY LOW",
					"LOW", "GOOD", "VERY GOOD", "EXCELENT" };
			int quality = _peer.getSignalQuality();
			out.println("Signal Quality : " + QUALITY[quality]);
		} catch (Exception e) {
			_log.error(e);
		}
		return 0;
	}
	
	// show attachement
	public final static String USAGE_SHOWATTACHEMENT = "";
	public final static String[] HELP_SHOWATTACHEMENT = new String[] { "Check if GSM / GPRS is attached" };

	public int cmdShowattachement(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("GSM  :"+(_peer.isGsmAttached()?"ok":"NO"));
		out.println("GPRS :"+(_peer.isGprsAttached()?"ok":"NO"));
		return 0;
	}
	

}
