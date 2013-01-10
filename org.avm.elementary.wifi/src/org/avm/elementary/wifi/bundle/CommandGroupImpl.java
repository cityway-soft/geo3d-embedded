package org.avm.elementary.wifi.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.wifi.WifiManager;
import org.avm.elementary.wifi.WifiManagerConfig;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "wifi-manager";

	private WifiManager _peer;

	CommandGroupImpl(ComponentContext context, WifiManager peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for wifi device.");
		_peer = peer;
	}

	// set balise list
	public final static String USAGE_SETBALISELIST = "<list>";

	public final static String[] HELP_SETBALISELIST = new String[] { "Set balise list", };

	public int cmdSetbaliselist(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String list = ((String) opts.get("list")).trim();
		((WifiManagerConfig) _config).setBaliseList(list);
		_config.updateConfig();

		return 0;
	}

	// get balise list
	public final static String USAGE_SHOWBALISELIST = "";

	public final static String[] HELP_SHOWBALISELIST = new String[] { "Show balise list", };

	public int cmdShowbaliselist(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Balise list:"
				+ ((WifiManagerConfig) _config).getBaliseList());
		return 0;
	}

	// set disconnect timeout
	public final static String USAGE_SETDISCONNECTTIMEOUT = "<timeout>";

	public final static String[] HELP_SETDISCONNECTTIMEOUT = new String[] { "Set elapse time before disconnect", };

	public int cmdSetdisconnecttimeout(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String timeout = ((String) opts.get("timeout")).trim();
		((WifiManagerConfig) _config).setDisconnectTimeout(timeout);
		_config.updateConfig();
		return 0;
	}

	// get disconnect timeout
	public final static String USAGE_SHOWDISCONNECTTIMEOUT = "";

	public final static String[] HELP_SHOWDISCONNECTTIMEOUT = new String[] { "Show disconnect timeout", };

	public int cmdShowdisconnecttimeout(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Timeout:"
				+ ((WifiManagerConfig) _config).getDisconnectTimeout());
		return 0;
	}

}
