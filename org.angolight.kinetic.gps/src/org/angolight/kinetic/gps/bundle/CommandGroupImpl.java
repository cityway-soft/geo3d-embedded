package org.angolight.kinetic.gps.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.angolight.kinetic.gps.KineticConfig;
import org.angolight.kinetic.gps.KineticServiceImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "kinetic";

	private KineticServiceImpl _peer;

	CommandGroupImpl(ComponentContext context, KineticServiceImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for kinetic.");
		_peer = (KineticServiceImpl) peer;

	}

	// minimum speed up
	public final static String USAGE_SETMINIMUMSPEEDUP = "<speed>";

	public final static String[] HELP_SETMINIMUMSPEEDUP = new String[] { "Set minimum speed up", };

	public int cmdSetminimumspeedup(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double speed = Double.parseDouble(((String) opts.get("speed")).trim());
		((KineticConfig) _config).setMinimumSpeedUp(speed);
		_config.updateConfig();

		out.println("Current minimum speed up : "
				+ ((KineticConfig) _config).getMinimumSpeedUp());
		return 0;
	}

	public final static String USAGE_SHOWMINIMUMSPEEDUP = "";

	public final static String[] HELP_SHOWMINIMUMSPEEDUP = new String[] { "Show current minimum speed up", };

	public int cmdShowminimumspeedup(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current minimum speed up : "
				+ ((KineticConfig) _config).getMinimumSpeedUp());
		return 0;
	}

	// minimum speed down
	public final static String USAGE_SETMINIMUMSPEEDDOWN = "<speed>";

	public final static String[] HELP_SETMINIMUMSPEEDDOWN = new String[] { "Set minimum speed down", };

	public int cmdSetminimumspeeddown(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double speed = Double.parseDouble(((String) opts.get("speed")).trim());
		((KineticConfig) _config).setMinimumSpeedDown(speed);
		_config.updateConfig();

		out.println("Current minimum speed down : "
				+ ((KineticConfig) _config).getMinimumSpeedDown());
		return 0;
	}

	public final static String USAGE_SHOWMINIMUMSPEEDDOWN = "";

	public final static String[] HELP_SHOWMINIMUMSPEEDDOWN = new String[] { "Show current minimum speed down", };

	public int cmdShowminimumspeeddown(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current minimum speed down : "
				+ ((KineticConfig) _config).getMinimumSpeedDown());
		return 0;
	}
}
