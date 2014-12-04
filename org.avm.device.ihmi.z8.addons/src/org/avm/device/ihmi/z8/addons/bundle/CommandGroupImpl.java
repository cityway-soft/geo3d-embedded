package org.avm.device.ihmi.z8.addons.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.ihmi.z8.Z8Access;
import org.avm.device.ihmi.z8.Z8Helper;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String CATEGORY = "org.avm.device.ihmi.addons";

	public static final String COMMAND_GROUP = "ihmi.z8";

	CommandGroupImpl(ComponentContext context) {
		super(context, null, COMMAND_GROUP,
				"Configuration commands for ihmi Z8.");
	}

	// get system state
	public final static String USAGE_GETSYSTEMSTATE = "";

	public final static String[] HELP_GETSYSTEMSTATE = new String[] { "Get system state", };

	public int cmdGetsystemstate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int state = Z8Access.getSystemCurrentState();
		out.println("Current state : " + Z8Helper.systemStateToText(state)
				+ "(" + state + ")");
		return 0;
	}

	// get board temperature
	public final static String USAGE_GETBOARDTEMPERATURE = "";

	public final static String[] HELP_GETBOARDTEMPERATURE = new String[] { "Get board temperature in Celsius degree", };

	public int cmdGetboardtemperature(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		int temp = Z8Access.getBoardTemperature();
		out.println(temp);
		return 0;
	}

	// get board temperature
	public final static String USAGE_GETPOWERSUPPLYLEVEL = "";

	public final static String[] HELP_GETPOWERSUPPLYLEVEL = new String[] { "Get Power supply level in V", };

	public int cmdGetpowersupplylevel(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		float temp = Z8Access.getPowerSuplyLevel();
		out.println(temp);
		return 0;
	}

	// get backlight
	public final static String USAGE_GETBACKLIGHTLEVEL = "";

	public final static String[] HELP_GETBACKLIGHTLEVEL = new String[] { "Get backlight level 1-253", };

	public int cmdGetbacklightlevel(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		int temp = Z8Access.getBacklightLevel();
		out.println(temp);
		return 0;
	}

	// get backlight
	public final static String USAGE_SETBACKLIGHTLEVEL = "<backlight>";

	public final static String[] HELP_SETBACKLIGHTLEVEL = new String[] { "Set backlight level 1-253", };

	public int cmdSetbacklightlevel(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String backlight = (String) opts.get("backlight");
		try {
			int val = Integer.parseInt(backlight);
			Z8Access.setBacklightLevel(val);
		} catch (NumberFormatException nfe) {
			out.println("Invalid number " + backlight);
		}
		return 0;
	}

	// get light
	public final static String USAGE_GETLIGHTLEVEL = "";

	public final static String[] HELP_GETLIGHTLEVEL = new String[] { "Get light level 1-255", };

	public int cmdGetlightlevel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int temp = Z8Access.getLightLevel();
		out.println(temp);
		return 0;
	}
}
