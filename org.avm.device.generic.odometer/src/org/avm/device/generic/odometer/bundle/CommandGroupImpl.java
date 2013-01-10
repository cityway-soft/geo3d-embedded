package org.avm.device.generic.odometer.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.generic.odometer.OdometerConfig;
import org.avm.device.odometer.Odometer;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String CATEGORY = "org.avm.device.odometer";

	public static final String COMMAND_GROUP = "odometer";

	private ConfigImpl _config;

	private Odometer _peer;

	CommandGroupImpl(ComponentContext context, Odometer peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the gps.");
		_peer = peer;
	}

	// Counter factor
	public final static String USAGE_SETCOUNTERFACTOR = "<factor>";

	public final static String[] HELP_SETCOUNTERFACTOR = new String[] { "Set counter factor", };

	public int cmdSetcounterfactor(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String str = ((String) opts.get("factor")).trim();
		double factor = Double.parseDouble(str);
		((OdometerConfig) _config).setCounterFactor(new Double(factor));
		_config.updateConfig();

		out.println("Current URL connection : "
				+ ((OdometerConfig) _config).getCounterFactor());
		return 0;
	}

	public final static String USAGE_SHOWCONTERFACTOR = "";

	public final static String[] HELP_SHOWCOUNTERFACTOR = new String[] { "Show current counter factor", };

	public int cmdShowcounterfactor(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current factor : " + _config.getCounterFactor());
		return 0;
	}

	// Speed limit
	public final static String USAGE_SETSPEEDLIMIT = "<speed>";

	public final static String[] HELP_SETSPEEDLIMIT = new String[] { "Set speed limit", };

	public int cmdSetspeedlimit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String str = ((String) opts.get("speed")).trim();
		double speed = Double.parseDouble(str);
		((OdometerConfig) _config).setSpeedLimit(new Double(speed));
		_config.updateConfig();

		out.println("Current speed limit : "
				+ ((OdometerConfig) _config).getSpeedLimit());
		return 0;
	}

	public final static String USAGE_SHOWSPEEDLIMIT = "";

	public final static String[] HELP_SHOWSPEEDLIMIT = new String[] { "Show current speed limit", };

	public int cmdShowspeedlimit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current Speed limit : " + _config.getSpeedLimit());
		return 0;
	}

}
