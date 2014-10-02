package org.avm.elementary.fonts.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.fonts.FontsConfig;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String CATEGORY = "org.avm.elementary.fonts";

	public static final String COMMAND_GROUP = "fonts";

	public CommandGroupImpl(ComponentContext context, AbstractConfig config) {
		super(context, config, COMMAND_GROUP, "Configuration for fonts.");
		
	}

	public final static String USAGE_SETFONTSPATH = "<fontsPath>";
	public final static String[] HELP_SETFONTSPATH = new String[] { "set fonts path url" };

	public int cmdSetfontspath(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String fonts = (String) opts.get("fontsPath");
		((FontsConfig) _config).setFontsPath(fonts);
		return 0;
	}

	public final static String USAGE_GETFONTSPATH = "";
	public final static String[] HELP_GETFONTSPATH = new String[] { "get fonts path url" };

	public int cmdGetfontspath(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current fonts path : "
				+ ((FontsConfig) _config).getFontsPath());
		return 0;
	}
}
