package org.avm.device.knet.bearer.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.knet.bearer.BearerManager;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String LOGGER = "org.avm.device.knet.bearerManager";
	public static final String COMMAND_GROUP = "knet.bearerManager";
	private ConfigImpl _config;
	private BearerManager _peer;

	protected CommandGroupImpl(ComponentContext context, BearerManager peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Commandes de configuration pour bearer manager");
		_peer = peer;
		_config = (ConfigImpl) config;
	}

	// Handover
	public final static String USAGE_SETBEARER2WLAN = "<ssid><wepkey>";
	public final static String[] HELP_SETBEARER2WLAN = new String[] { "Set Bearer to WLAN", };

	public int cmdSetbearer2wlan(Dictionary opts, Reader in, PrintWriter out,
			Session sessionlogin) {
		String ssid = (String) opts.get("ssid");
		String wepkey = (String) opts.get("wepkey");

		_peer.handover(BearerManager.BEARER_wlan, ssid, wepkey);
		out.println("Handover vers : " + _peer.getCurrentBearerName());
		return 0;
	}

	// GetBearer
	public final static String USAGE_GETBEARER = "";
	public final static String[] HELP_GETBEARER = new String[] { "Get current Bearer.", };

	public int cmdGetbearer(Dictionary opts, Reader in, PrintWriter out,
			Session sessionlogin) {
		String bearer = _peer.getCurrentBearerName();
		out.println("Current bearer : " + bearer);
		return 0;
	}

}
