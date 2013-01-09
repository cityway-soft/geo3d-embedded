package org.angolight.halfcycle.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.angolight.halfcycle.HalfCycleService;
import org.angolight.halfcycle.impl.HalfCycleConfig;
import org.angolight.halfcycle.impl.HalfCycleServiceImpl;
import org.angolight.kinetic.Kinetic;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "halfcycle";

	private HalfCycleServiceImpl _peer;

	CommandGroupImpl(ComponentContext context, HalfCycleService peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for halfcycle.");
		_peer = (HalfCycleServiceImpl) peer;

	}

	// filename
	public final static String USAGE_SETCURVESFILENAME = "<filename>";

	public final static String[] HELP_SETCURVESFILENAME = new String[] { "Set curves filename", };

	public int cmdSetcurvesfilename(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String filename = ((String) opts.get("filename")).trim();
		((HalfCycleConfig) _config).setFilename(filename);
		_config.updateConfig();

		out.println("Current curves filename : "
				+ ((HalfCycleConfig) _config).getFilename());
		return 0;
	}

	public final static String USAGE_SHOWCURVESFILENAME = "";

	public final static String[] HELP_SHOWCURVESFILENAME = new String[] { "Show current curves filename", };

	public int cmdShowcurvesfilename(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current curves filename : "
				+ ((HalfCycleConfig) _config).getFilename());
		return 0;
	}

	// minimum speed up
	public final static String USAGE_SETMINIMUMSPEEDUP = "<speed>";

	public final static String[] HELP_SETMINIMUMSPEEDUP = new String[] { "Set minimum speed up", };

	public int cmdSetminimumspeedup(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double speed = Double.parseDouble(((String) opts.get("speed")).trim());
		((HalfCycleConfig) _config).setMinimumSpeedUp(speed);
		_config.updateConfig();

		out.println("Current minimum speed up : "
				+ ((HalfCycleConfig) _config).getMinimumSpeedUp());
		return 0;
	}

	public final static String USAGE_SHOWMINIMUMSPEEDUP = "";

	public final static String[] HELP_SHOWMINIMUMSPEEDUP = new String[] { "Show current minimum speed up", };

	public int cmdShowminimumspeedup(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current minimum speed up : "
				+ ((HalfCycleConfig) _config).getMinimumSpeedUp());
		return 0;
	}

	// minimum speed down
	public final static String USAGE_SETMINIMUMSPEEDDOWN = "<speed>";

	public final static String[] HELP_SETMINIMUMSPEEDDOWN = new String[] { "Set minimum speed down", };

	public int cmdSetminimumspeeddown(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double speed = Double.parseDouble(((String) opts.get("speed")).trim());
		((HalfCycleConfig) _config).setMinimumSpeedDown(speed);
		_config.updateConfig();

		out.println("Current minimum speed down : "
				+ ((HalfCycleConfig) _config).getMinimumSpeedDown());
		return 0;
	}

	public final static String USAGE_SHOWMINIMUMSPEEDDOWN = "";

	public final static String[] HELP_SHOWMINIMUMSPEEDDOWN = new String[] { "Show current minimum speed down", };

	public int cmdShowminimumspeeddown(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current minimum speed down : "
				+ ((HalfCycleConfig) _config).getMinimumSpeedDown());
		return 0;
	}

	// positive acceleration up
	public final static String USAGE_SETPOSITIVEACCELERATIONUP = "<acceleration>";

	public final static String[] HELP_SETPOSITIVEACCELERATIONUP = new String[] { "Set positive acceleration up", };

	public int cmdSetpositiveaccelerationup(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double acceleration = Double.parseDouble(((String) opts
				.get("acceleration")).trim());
		((HalfCycleConfig) _config).setPositiveAccelerationUp(acceleration);
		_config.updateConfig();

		out.println("Current positive acceleration up : "
				+ ((HalfCycleConfig) _config).getPositiveAccelerationUp());
		return 0;
	}

	public final static String USAGE_SHOWPOSITIVEACCELERATIONUP = "";

	public final static String[] HELP_SHOWPOSITIVEACCELERATIONUP = new String[] { "Show current positive acceleration up", };

	public int cmdShowpositiveaccelerationup(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current positive acceleration up : "
				+ ((HalfCycleConfig) _config).getPositiveAccelerationUp());
		return 0;
	}

	// positive acceleration down
	public final static String USAGE_SETPOSITIVEACCELERATIONDOWN = "<acceleration>";

	public final static String[] HELP_SETPOSITIVEACCELERATIONDOWN = new String[] { "Set positive acceleration down", };

	public int cmdSetpositiveaccelerationdown(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double acceleration = Double.parseDouble(((String) opts
				.get("acceleration")).trim());
		((HalfCycleConfig) _config).setPositiveAccelerationDown(acceleration);
		_config.updateConfig();

		out.println("Current positive acceleration down : "
				+ ((HalfCycleConfig) _config).getPositiveAccelerationDown());
		return 0;
	}

	public final static String USAGE_SHOWPOSITIVEACCELERATIONDOWN = "";

	public final static String[] HELP_SHOWPOSITIVEACCELERATIONDOWN = new String[] { "Show current positive acceleration down", };

	public int cmdShowpositiveaccelerationdown(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current positive acceleration down : "
				+ ((HalfCycleConfig) _config).getPositiveAccelerationDown());
		return 0;
	}

	// negative acceleration up
	public final static String USAGE_SETNEGATIVEACCELERATIONUP = "<acceleration>";

	public final static String[] HELP_SETNEGATIVEACCELERATIONUP = new String[] { "Set negative acceleration up", };

	public int cmdSetnegativeaccelerationup(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double acceleration = Double.parseDouble(((String) opts
				.get("acceleration")).trim());
		((HalfCycleConfig) _config).setNegativeAccelerationUp(acceleration);
		_config.updateConfig();

		out.println("Current negative acceleration up : "
				+ ((HalfCycleConfig) _config).getNegativeAccelerationUp());
		return 0;
	}

	public final static String USAGE_SHOWNEGATIVEACCELERATIONUP = "";

	public final static String[] HELP_SHOWNEGATIVEACCELERATIONUP = new String[] { "Show current negative acceleration up", };

	public int cmdShownegativeaccelerationup(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current negative acceleration up : "
				+ ((HalfCycleConfig) _config).getNegativeAccelerationUp());
		return 0;
	}

	// negative acceleration down
	public final static String USAGE_SETNEGATIVEACCELERATIONDOWN = "<acceleration>";

	public final static String[] HELP_SETNEGATIVEACCELERATIONDOWN = new String[] { "Set negative acceleration down", };

	public int cmdSetnegativeaccelerationdown(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double acceleration = Double.parseDouble(((String) opts
				.get("acceleration")).trim());
		((HalfCycleConfig) _config).setNegativeAccelerationDown(acceleration);
		_config.updateConfig();

		out.println("Current negative acceleration down : "
				+ ((HalfCycleConfig) _config).getPositiveAccelerationDown());
		return 0;
	}

	public final static String USAGE_SHOWNEGATIVEACCELERATIONDOWN = "";

	public final static String[] HELP_SHOWNEGATIVEACCELERATIONDOWN = new String[] { "Show current negative acceleration down", };

	public int cmdShownegativeaccelerationdown(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current negative acceleration down : "
				+ ((HalfCycleConfig) _config).getNegativeAccelerationDown());
		return 0;
	}

	// kinetic
	public final static String USAGE_SETSTATES = "<speed> <acceleration>";

	public final static String[] HELP_SETSTATES = new String[] { "Set states [0-1FF] period [0-255] checksum[true,false]", };

	public int cmdSetstates(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		double acceleration = Double.parseDouble(((String) opts
				.get("acceleration")).trim());
		double speed = Double.parseDouble(((String) opts.get("speed")).trim());
		Kinetic kinetic = new Kinetic(speed, acceleration);
		_peer.onKinetic(kinetic);
		return 0;
	}
}
