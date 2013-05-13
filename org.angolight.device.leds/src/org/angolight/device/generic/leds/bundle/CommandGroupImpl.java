package org.angolight.device.generic.leds.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.angolight.device.generic.leds.LedsConfig;
import org.angolight.device.generic.leds.LedsImpl;
import org.angolight.device.leds.Leds;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "leds";

	private LedsImpl _peer;

	CommandGroupImpl(ComponentContext context, Leds peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the leds.");
		_peer = (LedsImpl) peer;
	}

	// connection
	public final static String USAGE_SETURLCONNECTION = "<uri>";

	public final static String[] HELP_SETURLCONNECTION = new String[] { "Set URL connection", };

	public int cmdSeturlconnection(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String uri = ((String) opts.get("uri")).trim();
		((LedsConfig) _config).setUrlConnection(uri);
		_config.updateConfig();

		out.println("Current URL connection : "
				+ ((LedsConfig) _config).getUrlConnection());
		return 0;
	}

	public final static String USAGE_SHOWURLCONNECTION = "";

	public final static String[] HELP_SHOWURLCONNECTION = new String[] { "Show current URL connection", };

	public int cmdShowurlconnection(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current URL connection : "
				+ ((LedsConfig) _config).getUrlConnection());
		return 0;
	}

	// brightness
	public final static String USAGE_SETBRIGHTNESS = "<value> [<check>]";

	public final static String[] HELP_SETBRIGHTNESS = new String[] { "Set brightness [0-255]", };

	public int cmdSetbrightness(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String text = ((String) opts.get("value"));
		Integer value = (text == null) ? new Integer(0) : Integer.valueOf(text
				.trim());
		text = ((String) opts.get("check"));
		boolean check = (text == null) ? false : Boolean.valueOf(text.trim())
				.booleanValue();

		((LedsConfig) _config).setBrightness(value);
		_config.updateConfig(false);

		_peer.L((byte) value.intValue(), check);

		out.println("Current brightness : "
				+ ((LedsConfig) _config).getBrightness());
		return 0;
	}

	public final static String USAGE_SHOWBRIGHTNESS = "";

	public final static String[] HELP_SHOWBRIGHTNESS = new String[] { "Show current brightness", };

	public int cmdShowbrightness(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current brightness : "
				+ ((LedsConfig) _config).getBrightness());
		return 0;
	}

	// current version
	public final static String USAGE_SHOWVERSION = "[<check>]";

	public final static String[] HELP_SHOWVERSION = new String[] { "Show current version checksum[true,false]", };

	public int cmdShowversion(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String text = ((String) opts.get("check"));
		boolean check = (text == null) ? false : Boolean.valueOf(text.trim())
				.booleanValue();
		long now = System.currentTimeMillis();

		int result = _peer.V(check);
		if (result < 0) {
			out.println("Echec show version");
		} else {
			int soft = result & 0xff;
			int hard = (result >> 8) & 0xff;
			out.println("Current version  : " + hard + "." + soft);
		}

//		out.println("Execution en "
//				+ (new Long(System.currentTimeMillis() - now) + " ms"));

		return 0;
	}

	// states
	public final static String USAGE_SETSTATES = "[<states>][<period>][<check>]";

	public final static String[] HELP_SETSTATES = new String[] { "Set states [0-1FF] period [0-255] checksum[true,false]", };

	public int cmdSetstates(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String text = ((String) opts.get("states"));
		short states = (text == null) ? 0 : Short.parseShort(text.trim(), 16);
		text = ((String) opts.get("period"));
		byte period = (text == null) ? 0 : Byte.parseByte(text.trim());
		text = ((String) opts.get("check"));
		boolean check = (text == null) ? false : Boolean.valueOf(text.trim())
				.booleanValue();

		long now = System.currentTimeMillis();
		_peer.M(states, period, check);
		System.out.println("Execution en "
				+ (new Long(System.currentTimeMillis() - now) + " ms"));

		return 0;
	}


	// read address
	public final static String USAGE_READADDRESS = "<address>[<check>]";

	public final static String[] HELP_READADDRESS = new String[] { "Read address [0-84] checksum[true,false]", };

	public int cmdReadaddress(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String text = null;
		text = ((String) opts.get("address"));
		byte address = (text == null) ? 0 : Byte.parseByte(text.trim(), 16);
		text = ((String) opts.get("check"));
		boolean check = (text == null) ? false : Boolean.valueOf(text.trim())
				.booleanValue();

		long now = System.currentTimeMillis();

		int result = _peer.R(address, check);
		if (result < 0) {
			out.println("[DSU ]echec read address");
		} else {
			int b0 = result & 0xff;
			int b1 = (result >> 8) & 0xff;
			int b2 = (result >> 16) & 0xff;
			out.println("Current states  : 0x" + b2 + "" + b1 + " period : "
					+ b0 * 10 + " ms");
		}

//		out.println("Execution en "
//				+ (new Long(System.currentTimeMillis() - now) + " ms"));

		return 0;
	}
}
