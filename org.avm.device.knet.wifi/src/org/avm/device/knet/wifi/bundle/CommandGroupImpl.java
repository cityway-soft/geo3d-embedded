package org.avm.device.knet.wifi.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.knet.wifi.WifiConfig;
import org.avm.device.wifi.Wifi;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String CATEGORY = "org.avm.device.wifi";

	public static final String COMMAND_GROUP = "wifi";

	private ConfigImpl _config;

	private Wifi _peer;

	CommandGroupImpl(ComponentContext context, Wifi peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for wifi device.");
		_peer = peer;
		_config = config;
	}

	public final static String USAGE_RESET2DEFAULT = "";
	public final static String[] HELP_RESET2DEFAULT = new String[] { "Reset values to default", };

	public int cmdReset2default(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_config.stop();
		_config.delete();
		_config.start();

		return 0;
	}

	// set SSID
	public final static String USAGE_SETSSID = "<ssid>";
	public final static String[] HELP_SETSSID = new String[] { "Set SSID", };

	public int cmdSetssid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String ssid = ((String) opts.get("ssid")).trim();
		((WifiConfig) _config).setSSID(ssid);
		_config.updateConfig();

		return 0;
	}

	public final static String USAGE_CONNECT = "";
	public final static String[] HELP_CONNECT = new String[] { "Initialize wifi interface." };

	public int cmdConnect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_peer.connect();
		return 0;
	}

	public final static String USAGE_DISCONNECT = "";
	public final static String[] HELP_DISCONNECT = new String[] { "Delete wifi interface." };

	public int cmdDisconnect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_peer.disconnect();
		return 0;
	}

	// set ad-hoc
	public final static String USAGE_SETADHOC = "<adhoc>";
	public final static String[] HELP_SETADHOC = new String[] { "Enable ad-hoc infrastructure", };

	public int cmdSetadhoc(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		boolean adhoc = ((Boolean) opts.get("adhoc")).booleanValue();
		((WifiConfig) _config).setAdhoc(adhoc);
		_config.updateConfig();

		return 0;
	}

	// set encryption
	public final static String USAGE_SETENCRYPTION = "<encryption>";
	public final static String[] HELP_SETENCRYPTION = new String[] { "Enable wep encryption", };

	public int cmdSetencryption(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		boolean encryption = ((Boolean) opts.get("encryption")).booleanValue();
		((WifiConfig) _config).setEncryption(encryption);
		_config.updateConfig();

		return 0;
	}

	// set authentication
	public final static String USAGE_SETAUTHENTICATION = "<authentication>";
	public final static String[] HELP_SETAUTHENTICATION = new String[] { "Enable an authentication process", };

	public int cmdSetauthentication(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		boolean authentication = ((Boolean) opts.get("authentication"))
				.booleanValue();
		((WifiConfig) _config).setAuthentication(authentication);
		_config.updateConfig();

		return 0;
	}

	// set EAP_TYPE
	public final static String USAGE_SETEAPTYPE = "<eaptype>";
	public final static String[] HELP_SETEAPTYPE = new String[] { "Set EAP authentication type", };

	public int cmdSeteaptype(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String eaptype = ((String) opts.get("eaptype")).trim();
		((WifiConfig) _config).setEAPtype(eaptype);
		_config.updateConfig();

		return 0;
	}

	// set AUTOMATIC_WEPKEY
	public final static String USAGE_SETAUTOMATICWEPKEY = "<automaticwepkey>";
	public final static String[] HELP_SETAUTOMATICWEPKEY = new String[] { "Enable automatic WEP key generation", };

	public int cmdSetautomaticwepkey(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		boolean automaticwepkey = ((Boolean) opts.get("automaticwepkey"))
				.booleanValue();
		((WifiConfig) _config).setAutomaticWEPKey(automaticwepkey);
		_config.updateConfig();

		return 0;
	}

	// set WEPKEY
	public final static String USAGE_SETWEPKEY = "<wepkey>";
	public final static String[] HELP_SETWEPKEY = new String[] { "Set WEP key", };

	public int cmdSetwepkey(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String wepkey = ((String) opts.get("wepkey")).trim();
		((WifiConfig) _config).setWEPKey(wepkey);
		_config.updateConfig();

		return 0;
	}

	// is connected?
	public final static String USAGE_ISCONNECTED = "";
	public final static String[] HELP_ISCONNECTED = new String[] { "Are we connected to a wifi network?" };

	public int cmdIsconnected(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("We are " + (_peer.isConnected() ? "" : "NOT! ")
				+ "connected to network.");

		return 0;
	}
}
