package org.angolight.hmi.swt.leds.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.angolight.device.leds.Leds;
import org.angolight.hmi.swt.leds.LedsImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "leds.swt";

	private LedsImpl _peer;

	CommandGroupImpl(ComponentContext context, Leds peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the leds.");
		_peer = (LedsImpl) peer;
	}

	public final static String USAGE_SETVISIBLE = "<visible>";

	public final static String[] HELP_SETVISIBLE = new String[] { "Show/Hide Leds SWT IHM", };

	public int cmdSetvisible(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		boolean visible = false;
		String svisible = ((String) opts.get("visible")).trim().toLowerCase();
		visible = svisible.equals("true");

		_peer.setVisible(visible);
		return 0;
	}

	// states
	public final static String USAGE_SETSTATES = "[<states>][<period>][<check>]";

	public final static String[] HELP_SETSTATES = new String[] { "Set states [0-1FF] period [0-255]", };

	public int cmdSetstates(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String text = ((String) opts.get("states"));
		short states = (text == null) ? 0 : Short.parseShort(text.trim(), 16);
		text = ((String) opts.get("period"));
		byte period = (text == null) ? 0 : Byte.parseByte(text.trim());

		long now = System.currentTimeMillis();
		_peer.M(states, period, false);
		out.println("Execution en "
				+ (new Long(System.currentTimeMillis() - now) + " ms"));

		return 0;
	}
	
	
	// states
	public final static String USAGE_INSIDE = "[<value>]";

	public final static String[] HELP_INSIDE = new String[] { "Set inside", };

	public int cmdInside(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String val = ((String) opts.get("value"));
		if (val == null){
			out.println("inside=" + ((ConfigImpl)_config).isInside());
		}
		else{
			((ConfigImpl)_config).setInside(val.equalsIgnoreCase("true"));
			_config.updateConfig();
		}

		return 0;
	}

}
