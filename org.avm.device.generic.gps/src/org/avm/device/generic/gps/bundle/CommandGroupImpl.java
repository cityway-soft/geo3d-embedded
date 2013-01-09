package org.avm.device.generic.gps.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;
import java.util.Dictionary;

import org.avm.device.generic.gps.GpsConfig;
import org.avm.device.gps.Gps;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.position.Position;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "gps";
	private Gps _peer;

	CommandGroupImpl(ComponentContext context, Gps peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the gps.");
		_peer = peer;
	}

	// URL connection
	public final static String USAGE_SETURLCONNECTION = "<uri>";
	public final static String[] HELP_SETURLCONNECTION = new String[] { "Set URL connection", };

	public int cmdSeturlconnection(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String uri = ((String) opts.get("uri")).trim();
		((GpsConfig) _config).setUrlConnection(uri);
		_config.updateConfig();
		out.println("Current URL connection : "
				+ ((GpsConfig) _config).getUrlConnection());
		return 0;
	}

	public final static String USAGE_SHOWURLCONNECTION = "";
	public final static String[] HELP_SHOWURLCONNECTION = new String[] { "Show current URL connection", };

	public int cmdShowurlconnection(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current URL connection : "
				+ ((GpsConfig) _config).getUrlConnection());
		return 0;
	}

	// Delay
	public final static String USAGE_SETDELAY = "<delay>";
	public final static String[] HELP_SETDELAY = new String[] { "Set delay (s)", };

	public int cmdSetdelay(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Double delay = new Double(((String) opts.get("delay")).trim());
		((GpsConfig) _config).setDelay(delay);
		_config.updateConfig();
		out.println("Current delay : " + ((GpsConfig) _config).getDelay());
		return 0;
	}

	public final static String USAGE_SHOWDELAY = "";
	public final static String[] HELP_SHOWDELAY = new String[] { "Show delay (s)", };

	public int cmdShowdelay(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current delay : " + ((GpsConfig) _config).getDelay());
		return 0;
	}

	// Correct
	public final static String USAGE_SETCORRECT = "<correct>";
	public final static String[] HELP_SETCORRECT = new String[] { "Set correct true/false", };

	public int cmdSetcorrect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Boolean correct = new Boolean(((String) opts.get("correct")).trim());
		((GpsConfig) _config).setCorrect(correct);
		_config.updateConfig();
		out
				.println("Current correct  : "
						+ ((ConfigImpl) _config).getCorrect());
		return 0;
	}

	public final static String USAGE_SHOWCORRECT = "";
	public final static String[] HELP_SHOWCORRECT = new String[] { "Show current  correct true/false", };

	public int cmdShowcorrect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current correct  : " + ((GpsConfig) _config).getCorrect());
		return 0;
	}

	public final static String USAGE_SHOWPOSITION = "";
	public final static String[] HELP_SHOWPOSITION = new String[] { "Show current gps position", };

	public int cmdShowposition(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Position p = _peer.getCurrentPosition();
		if (p != null) {
			Measurement lat = p.getLatitude();
			Measurement lon = p.getLongitude();
			Measurement alt = p.getAltitude();
			Measurement speed = p.getSpeed();
			Measurement cap = p.getTrack();
			out.println("Current position : ");
			out.println("   - time : " + new Date(lat.getTime()));
			out.println("   - latitude : " + lat.getValue() * (180d / Math.PI));
			out
					.println("   - longitude : " + lon.getValue()
							* (180d / Math.PI));
			out.println("   - altitude : " + alt.getValue());
			out.println("   - speed : " + speed.getValue() * 3.6d);
			out.println("   - cap : " + cap.getValue());
		} else {
			out.println("Current position is null !");
		}
		return 0;
	}
}
