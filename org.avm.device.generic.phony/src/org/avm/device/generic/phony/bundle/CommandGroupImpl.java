package org.avm.device.generic.phony.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.device.generic.phony.impl.PhonyConfig;
import org.avm.device.generic.phony.impl.PhonyImpl;
import org.avm.device.phony.Phony;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "phony";
	private Phony _peer;

	public CommandGroupImpl(ComponentContext context, Phony peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for phony.");
		_peer = peer;
	}

	// Dial
	public final static String USAGE_DIAL = "[-l] <number>";
	public final static String[] HELP_DIAL = new String[] { "Dial phone number" };

	public int cmdDial(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String number = ((String) opts.get("number")).trim();
		boolean listen = (opts.get("-l") == null) ? false : true;

		try {
			if(listen){
				_peer.dialListenMode(number);
			}else {
				_peer.dial(number);
			}
			
		} catch (Exception e) {
			out.println("Exception : " + e.getMessage());
		}
		return 0;
	}

	// Hang-up
	public final static String USAGE_HANGUP = "";
	public final static String[] HELP_HANGUP = new String[] { "Hang-up" };

	public int cmdHangup(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			_peer.hangup();
		} catch (Exception e) {
			out.println("Exception : " + e.getMessage());
		}
		return 0;
	}

	// Answer
	public final static String USAGE_ANSWER = "";
	public final static String[] HELP_ANSWER = new String[] { "Answer to the incomming call" };

	public int cmdAnswer(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			_peer.answer();
		} catch (Exception e) {
			out.println("Exception : " + e.getMessage());
		}
		return 0;
	}

	// set volume
	public final static String USAGE_SETVOLUME = "<volume>";
	public final static String[] HELP_SETVOLUME = new String[] { "Set volume" };

	public int cmdSetvolume(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int volume = new Integer(((String) opts.get("volume")).trim())
				.intValue();
		_peer.setVolume(volume);
		return 0;
	}

	// set default sound volume
	public final static String USAGE_SETDEFSOUNDVOLUME = "<volume>";
	public final static String[] HELP_SETDEFSOUNDVOLUME = new String[] { "Set default sound volume" };

	public int cmdSetdefsoundvolume(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		int volume = new Integer(((String) opts.get("volume")).trim())
				.intValue();
		((PhonyConfig) _config).setDefaultSoundVolume(volume);
		_config.updateConfig();
		return 0;
	}

	// set default sound volume
	public final static String USAGE_GETDEFSOUNDVOLUME = "";
	public final static String[] HELP_GETDEFSOUNDVOLUME = new String[] { "Get default sound volume" };

	public int cmdGetdefsoundvolume(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		int volume = ((PhonyConfig) _config).getDefaultSoundVolume();
		out.println("Default sound volume : " + volume);
		return 0;
	}

	// add specific command
	public final static String USAGE_ADDCOMMAND = "<id><atcommand><desc>";
	public final static String[] HELP_ADDCOMMAND = new String[] { "add specific command" };

	public int cmdAddcommand(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String id = (String) opts.get("id");
		String atcommand = (String) opts.get("atcommand");
		if (!atcommand.endsWith("\r")) {
			atcommand += "\r";
		}
		String desc = (String) opts.get("desc");
		((PhonyConfig) _config).addSpecificAtCommand(id, atcommand, desc);
		_config.updateConfig();
		return 0;
	}

	// remove specific command
	public final static String USAGE_REMOVECOMMAND = "<id>";
	public final static String[] HELP_REMOVECOMMAND = new String[] { "remove specific command" };

	public int cmdRemovecommand(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String id = (String) opts.get("id");
		((PhonyConfig) _config).removeSpecificAtCommand(id);
		_config.updateConfig();
		return 0;
	}

	// list commands
	public final static String USAGE_LIST = "";
	public final static String[] HELP_LIST = new String[] { "list commands" };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Properties p = ((ConfigImpl) _config)
				.getSpecificCommandProperties(null);
		Enumeration e = p.keys();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Properties props = (Properties) p.get(key);
			out.print(key.substring(PhonyConfig.TAG_AT.length()));
			out.print(": ");
			out.print(((String) props.get(PhonyConfig.TAG_CMD_REQUEST)).trim());
			out.print(" (");
			out.print(props.get(PhonyConfig.TAG_CMD_DESC));
			out.println(") ");

		}
		return 0;
	}

	// set init commands
	public final static String USAGE_SETINIT = "[<list>]";
	public final static String[] HELP_SETINIT = new String[] { "command list separated with ','" };

	public int cmdSetinit(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String list = (String) opts.get("list");
		if (list != null) {
			StringTokenizer t = new StringTokenizer(list, ",");
			String[] result = new String[t.countTokens()];
			int i = 0;
			while (t.hasMoreElements()) {
				String s = (String) t.nextElement();
				result[i] = s;
				i++;
			}
			((PhonyConfig) _config).setInitAtCommand(result);
		} else {
			String[] result = ((PhonyConfig) _config).getInitAtCommand();
			for (int i = 0; i < result.length; i++) {
				out.print(result[i]);
				out.print(", ");
			}
			out.println();
		}
		return 0;
	}

	// at
	public final static String USAGE_AT = "<cmd>";
	public final static String[] HELP_AT = new String[] { "send predefined at command" };

	public int cmdAt(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String cmd = (String) opts.get("cmd")+"\r";
		String result = ((PhonyImpl) _peer).at(cmd);
		out.println(result);
		return 0;
	}

}
