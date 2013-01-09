package org.avm.device.linux.wifi.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.linux.wifi.WifiConfig;
import org.avm.device.wifi.Wifi;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "wifi";
	private Wifi _peer;

	CommandGroupImpl(ComponentContext context, Wifi peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for wifi device.");
		_peer = peer;
	}

	// device
	public final static String USAGE_SETDEVICE = "<device>";
	public final static String[] HELP_SETDEVICE = new String[] { "Set device", };

	public int cmdSetdevice(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String device = ((String) opts.get("device")).trim();
		((WifiConfig) _config).setDevice(device);
		_config.updateConfig();
		out.println("Current device : " + ((WifiConfig) _config).getDevice());
		return 0;
	}

	public final static String USAGE_SHOWDEVICE = "";
	public final static String[] HELP_SHOWDEVICE = new String[] { "Show current device", };

	public int cmdShowdevice(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current device : " + ((WifiConfig) _config).getDevice());
		return 0;
	}

	// essid
	public final static String USAGE_SETESSID = "<essid>";
	public final static String[] HELP_SETESSID = new String[] { "Set essid", };

	public int cmdSetessid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String essid = ((String) opts.get("essid")).trim();
		((WifiConfig) _config).setEssid(essid);
		_config.updateConfig();
		out.println("Current essid : " + ((WifiConfig) _config).getEssid());
		return 0;
	}

	public final static String USAGE_SHOWESSID = "";
	public final static String[] HELP_SHOWESSID = new String[] { "Show current essid", };

	public int cmdShowessid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current essid : " + ((WifiConfig) _config).getEssid());
		return 0;
	}

	// key
	public final static String USAGE_SETKEY = "<key>";
	public final static String[] HELP_SETKEY = new String[] { "Set key", };

	public int cmdSetkey(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String key = ((String) opts.get("key")).trim();
		((WifiConfig) _config).setKey(key);
		_config.updateConfig();
		out.println("Current key : " + ((WifiConfig) _config).getKey());
		return 0;
	}

	public final static String USAGE_SHOWKEY = "";
	public final static String[] HELP_SHOWKEY = new String[] { "Show current key", };

	public int cmdShowkey(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current key : " + ((WifiConfig) _config).getKey());
		return 0;
	}

	// rate
	public final static String USAGE_SETRATE = "<rate>";
	public final static String[] HELP_SETRATE = new String[] { "Set rate", };

	public int cmdSetrate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String rate = ((String) opts.get("rate")).trim();
		((WifiConfig) _config).setRate(rate);
		_config.updateConfig();
		out.println("Current rate : " + ((WifiConfig) _config).getRate());
		return 0;
	}

	public final static String USAGE_SHOWRATE = "";
	public final static String[] HELP_SHOWRATE = new String[] { "Show current rate", };

	public int cmdShowrate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current rate : " + ((WifiConfig) _config).getRate());
		return 0;
	}

	// mode
	public final static String USAGE_SETMODE = "<mode>";
	public final static String[] HELP_SETMODE = new String[] { "Set mode", };

	public int cmdSetmode(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String mode = ((String) opts.get("mode")).trim();
		((WifiConfig) _config).setMode(mode);
		_config.updateConfig();
		out.println("Current mode : " + ((WifiConfig) _config).getMode());
		return 0;
	}

	public final static String USAGE_SHOWMODE = "";
	public final static String[] HELP_SHOWMODE = new String[] { "Show current mode", };

	public int cmdShowmode(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current mode : " + ((WifiConfig) _config).getMode());
		return 0;
	}

	public final static String USAGE_CONNECT = "";
	public final static String[] HELP_CONNECT = new String[] { "Connect to defined access point", };

	public int cmdConnect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_peer.connect();
		return 0;
	}

	public final static String USAGE_DISCONNECT = "";
	public final static String[] HELP_DISCONNECT = new String[] { "Disconnect to defined access point", };

	public int cmdDisconnect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_peer.disconnect();
		return 0;
	}

	public final static String USAGE_ISCONNECTED = "";
	public final static String[] HELP_ISCONNECTED = new String[] { "Test if platform is connected to defined access point", };

	public int cmdIsconnected(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(_peer.isConnected()?"Connected":"Not Connected");
		return 0;
	}

}
