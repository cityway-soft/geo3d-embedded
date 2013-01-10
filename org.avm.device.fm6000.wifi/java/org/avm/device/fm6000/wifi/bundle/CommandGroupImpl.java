package org.avm.device.fm6000.wifi.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Properties;

import org.avm.device.fm6000.wifi.WifiConfig;
import org.avm.device.wifi.Wifi;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String COMMAND_GROUP = "wifi";

	private Wifi _peer;

	private String _ssid;

	private Properties _properties;

	CommandGroupImpl(ComponentContext context, Wifi peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for wifi device.");
		_peer = peer;
	}

	// add SSID
	public final static String USAGE_ADDSSID = "<ssid>";

	public final static String[] HELP_ADDSSID = new String[] { "Add SSID", };

	public int cmdAddssid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_properties = new Properties();
		_ssid = ((String) opts.get("ssid")).trim();
		_properties.put(WifiConfig.TAG_ENCRYPTION,
				WifiConfig.DEFAULT_ENCRYPTION);
		_properties.put(WifiConfig.TAG_OPEN_AUTHENTICATION,
				WifiConfig.DEFAULT_OPEN_AUTHENTICATION);
		_properties.put(WifiConfig.TAG_ADHOC, WifiConfig.DEFAULT_ADHOC);
		_properties.put(WifiConfig.TAG_WEPKEY, WifiConfig.DEFAULT_WEPKEY);
		_properties.put(WifiConfig.TAG_AUTOMATIC_WEPKEY,
				WifiConfig.DEFAULT_AUTOMATIC_WEPKEY);
		_properties.put(WifiConfig.TAG_AUTHENTICATION,
				WifiConfig.DEFAULT_AUTHENTICATION);
		_properties.put(WifiConfig.TAG_EAP_TYPE, WifiConfig.DEFAULT_EAP_TYPE);
		out.println("Current selection : " + _ssid);
		out.println("Don't forget 'updateconfig' when finished!");
		return 0;
	}

	public final static String USAGE_CONFIG = "";

	public final static String[] HELP_CONFIG = new String[] { "Show current config", };

	public int cmdConfig(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(_config);
		return 0;
	}

	public final static String USAGE_CONNECT = "";

	public final static String[] HELP_CONNECT = new String[] { "Initialize wifi interface." };

	public int cmdConnect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			_peer.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public final static String USAGE_DISCONNECT = "";

	public final static String[] HELP_DISCONNECT = new String[] { "Delete wifi interface." };

	public int cmdDisconnect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			_peer.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// set ad-hoc
	public final static String USAGE_SETADHOC = "<adhoc>";

	public final static String[] HELP_SETADHOC = new String[] { "Enable ad-hoc infrastructure", };

	public int cmdSetadhoc(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		if (_ssid != null && _properties != null) {
			boolean adhoc = Boolean.valueOf(((String) opts.get("adhoc")))
					.booleanValue();
			_properties.put(WifiConfig.TAG_ADHOC, "" + adhoc);
		} else {
			out.println("no ssid selected.");
		}

		return 0;
	}

	// set encryption
	public final static String USAGE_SETENCRYPTION = "<encryption>";

	public final static String[] HELP_SETENCRYPTION = new String[] { "Enable wep encryption", };

	public int cmdSetencryption(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		if (_ssid != null && _properties != null) {
			boolean encryption = Boolean.valueOf(
					((String) opts.get("encryption"))).booleanValue();
			_properties.put(WifiConfig.TAG_ENCRYPTION, "" + encryption);
		} else {
			out.println("no ssid selected.");
		}

		return 0;
	}

	// set authentication
	public final static String USAGE_SETAUTHENTICATION = "<authentication>";

	public final static String[] HELP_SETAUTHENTICATION = new String[] { "Enable an authentication process", };

	public int cmdSetauthentication(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		if (_ssid != null && _properties != null) {
			boolean authentication = Boolean.valueOf(
					((String) opts.get("authentication"))).booleanValue();
			_properties.put(WifiConfig.TAG_AUTHENTICATION, "" + authentication);
		} else {
			out.println("no ssid selected.");
		}

		return 0;
	}

	// set EAP_TYPE
	public final static String USAGE_SETEAPTYPE = "<eaptype>";

	public final static String[] HELP_SETEAPTYPE = new String[] { "Set EAP authentication type", };

	public int cmdSeteaptype(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		if (_ssid != null && _properties != null) {
			String eaptype = ((String) opts.get("eaptype")).trim();
			_properties.put(WifiConfig.TAG_EAP_TYPE, eaptype);
		} else {
			out.println("no ssid selected.");
		}

		return 0;
	}

	// set AUTOMATIC_WEPKEY
	public final static String USAGE_SETAUTOMATICWEPKEY = "<automaticwepkey>";

	public final static String[] HELP_SETAUTOMATICWEPKEY = new String[] { "Enable automatic WEP key generation", };

	public int cmdSetautomaticwepkey(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		if (_ssid != null && _properties != null) {
			boolean automaticwepkey = Boolean.valueOf(
					(String) opts.get("automaticwepkey")).booleanValue();
			_properties.put(WifiConfig.TAG_AUTOMATIC_WEPKEY, ""
					+ automaticwepkey);
		} else {
			out.println("no ssid selected.");
		}

		return 0;
	}

	// set WEPKEY
	public final static String USAGE_SETWEPKEY = "<wepkey>";

	public final static String[] HELP_SETWEPKEY = new String[] { "Set WEP key", };

	public int cmdSetwepkey(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		if (_ssid != null && _properties != null) {
			String wepkey = ((String) opts.get("wepkey")).trim();
			_properties.put(WifiConfig.TAG_WEPKEY, wepkey);
		} else {
			out.println("no ssid selected.");
		}

		return 0;
	}

	// is connected?
	public final static String USAGE_ISCONNECTED = "";

	public final static String[] HELP_ISCONNECTED = new String[] { "Are we connected to a wifi network?" };

	public int cmdIsconnected(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String adr = null;

		out.println("We are " + (_peer.isConnected() ? "" : "NOT! ")
				+ "connected to network.");

		adr = _peer.getProperties().getProperty("ip");
		if (adr != null) {
			out.println("Our IP is " + adr);
		}
		return 0;
	}

	// Update config
	public final static String USAGE_SELECTSSID = "[<ssid>]";

	public final static String[] HELP_SELECTSSID = new String[] { "Select ssid", };

	public int cmdSelectssid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String ssid = ((String) opts.get("ssid"));
		if (ssid != null) {
			Properties p = ((WifiConfig) _config).getProperty(ssid);
			if (p == null) {
				out.println("no ssid '" + ssid + "' in configuration");
				return 0;
			} else {
				_ssid = ssid;
				_properties = p;
			}
		}
		if (_ssid == null || _properties == null) {
			out.println("no ssid selected.");
		} else {
			out.println("Selected ssid " + _ssid + " " + _properties);
		}

		return 0;
	}

}
