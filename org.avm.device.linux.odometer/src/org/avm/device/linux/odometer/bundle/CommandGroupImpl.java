package org.avm.device.linux.odometer.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.linux.odometer.OdometerConfig;
import org.avm.device.odometer.Odometer;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "odometer";
	private Odometer _peer;

	CommandGroupImpl(ComponentContext context, Odometer peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the odometer.");
		_peer = peer;
	}

	// Counter factor
	public final static String USAGE_SETCOUNTERFACTOR = "<factor>";

	public final static String[] HELP_SETCOUNTERFACTOR = new String[] { "Set counter factor", };

	public int cmdSetcounterfactor(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String str = ((String) opts.get("factor")).trim();
		double factor = Double.parseDouble(str);
		((OdometerConfig) _config).setCounterFactor(factor);
		_config.updateConfig();

		out.println("Current URL connection : "
				+ ((OdometerConfig) _config).getCounterFactor());
		return 0;
	}

	public final static String USAGE_SHOWCONTERFACTOR = "";

	public final static String[] HELP_SHOWCOUNTERFACTOR = new String[] { "Show current counter factor", };

	public int cmdShowcounterfactor(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current factor : "
				+ ((OdometerConfig) _config).getCounterFactor());
		return 0;
	}

	// Speed limit
	public final static String USAGE_SETSPEEDLIMIT = "<speed>";

	public final static String[] HELP_SETSPEEDLIMIT = new String[] { "Set speed limit", };

	public int cmdSetspeedlimit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String str = ((String) opts.get("speed")).trim();
		double speed = Double.parseDouble(str);
		((OdometerConfig) _config).setSpeedLimit(speed);
		_config.updateConfig();

		out.println("Current speed limit : "
				+ ((OdometerConfig) _config).getSpeedLimit());
		return 0;
	}

	public final static String USAGE_SHOWSPEEDLIMIT = "";

	public final static String[] HELP_SHOWSPEEDLIMIT = new String[] { "Show current speed limit", };

	public int cmdShowspeedlimit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current Speed limit : "
				+ ((OdometerConfig) _config).getSpeedLimit());
		return 0;
	}

	// Track limit
	public final static String USAGE_SETTRACKLIMIT = "<track>";

	public final static String[] HELP_SETTRACKLIMIT = new String[] { "Set track limit", };

	public int cmdSettracklimit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String str = ((String) opts.get("track")).trim();
		double track = Double.parseDouble(str);
		((OdometerConfig) _config).setSpeedLimit(track);
		_config.updateConfig();

		out.println("Current Track limit : "
				+ ((OdometerConfig) _config).getTrackLimit());
		return 0;
	}

	public final static String USAGE_SHOWTRACKLIMIT = "";

	public final static String[] HELP_SHOWTRACKLIMIT = new String[] { "Show current track limit", };

	public int cmdShowtracklimit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current Track limit : "
				+ ((OdometerConfig) _config).getTrackLimit());
		return 0;
	}

	// Sample limit
	public final static String USAGE_SETSAMPLELIMIT = "<sample>";

	public final static String[] HELP_SETSAMPLELIMIT = new String[] { "Set sample limit", };

	public int cmdSetsamplelimit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String str = ((String) opts.get("sample")).trim();
		int sample = Integer.parseInt(str);
		((OdometerConfig) _config).setSampleLimit(sample);
		_config.updateConfig();

		out.println("Current sample limit : "
				+ ((OdometerConfig) _config).getSampleLimit());
		return 0;
	}

	public final static String USAGE_SHOWSAMPLELIMIT = "";

	public final static String[] HELP_SHOWSAMPLELIMIT = new String[] { "Show current sample limit", };

	public int cmdShowsamplelimit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current Sample limit : "
				+ ((OdometerConfig) _config).getSampleLimit());
		return 0;
	}

}
