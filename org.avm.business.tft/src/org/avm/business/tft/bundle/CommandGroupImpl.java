package org.avm.business.tft.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.business.tft.Tft;
import org.avm.business.tft.TftConfig;
import org.avm.business.tft.TftImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String CATEGORY = "org.avm.business.tft";

	public static final String COMMAND_GROUP = "tft";

	private static String[] units = { "px", "cm", "em", "%" };

	private static String availableUnits;
	
	private TftImpl _peer;
	
	static {
		StringBuffer tmp = new StringBuffer ();
		for (int i =0; i < units.length; ++i){
			tmp.append(units[i]);
			if (i < units.length -1 ){
				tmp.append(", ");
			}
		}
		availableUnits =tmp.toString();
	}
	

	CommandGroupImpl(ComponentContext context, Tft peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for 'tft'.");
		_peer = (TftImpl) peer;
	}

	// set font size
	public final static String USAGE_SETFONTSIZE = "<fontSize>";

	public final static String[] HELP_SETFONTSIZE = new String[] { "Set font size, set size and unit", };

	public int cmdSetfontsize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String fontSize = (String) opts.get("fontSize");

		boolean update = false;
		for (int i = 0; i < units.length; ++i) {
			if (fontSize.endsWith(units[i])) {
				update = true;
			}
		}
		if (update) {
			((TftConfig) _config).setFontSize(fontSize);
			_config.updateConfig();

			out.println("Current font size : "
					+ ((TftConfig) _config).getFontSize());
		} else {
			out.println("Invalid unit. Unit available : " +availableUnits);
		}
		return 0;
	}

	// get font size
	public final static String USAGE_GETFONTSIZE = "";

	public final static String[] HELP_GETFONTSIZE = new String[] { "Get font size", };

	public int cmdGetfontsize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current font size : "
				+ ((TftConfig) _config).getFontSize());
		return 0;
	}

	// set font name
	public final static String USAGE_SETFONTNAME = "<fontName>";

	public final static String[] HELP_SETFONTNAME = new String[] { "Set font name", };

	public int cmdSetfontname(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String fontSize = (String) opts.get("fontSize");

		((TftConfig) _config).setFontSize(fontSize);
		_config.updateConfig();

		out.println("Current font size : "
				+ ((TftConfig) _config).getFontSize());
		return 0;
	}

	// get font name
	public final static String USAGE_GETFONTNAME = "";

	public final static String[] HELP_GETFONTNAME = new String[] { "Get font name", };

	public int cmdGetfontname(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current font name : "
				+ ((TftConfig) _config).getFontName());
		return 0;
	}
}
