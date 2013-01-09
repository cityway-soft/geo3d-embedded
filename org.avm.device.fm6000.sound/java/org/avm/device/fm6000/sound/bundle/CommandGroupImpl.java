package org.avm.device.fm6000.sound.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Properties;

import org.avm.device.fm6000.sound.SoundConfig;
import org.avm.device.sound.Sound;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "sound";
	private Sound _peer;

	CommandGroupImpl(ComponentContext context, Sound peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the sound.");
		_peer = peer;
	}

	// Add
	public final static String USAGE_ADD = "-n #name# -f #url# -p #priority# ";
	public final static String[] HELP_ADD = new String[] { "Add config", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("-n")).trim();
		String url = ((String) opts.get("-f")).trim();
		String priority = ((String) opts.get("-p")).trim();
		Properties p = new Properties();
		p.put(Sound.NAME, name);
		p.put(Sound.URL, url);
		p.put(Sound.PRIORITY, priority);
		((SoundConfig) _config).add(p);
		cmdUpdateconfig(opts, in, out, session);
		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-n #name#";
	public final static String[] HELP_REMOVE = new String[] { "Remove config", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("-n")).trim();
		((SoundConfig) _config).remove(name);
		cmdUpdateconfig(opts, in, out, session);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";
	public final static String[] HELP_LIST = new String[] { "List all config", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println((SoundConfig) _config);
		return 0;
	}

	// Configure
	public final static String USAGE_CONFIGURE = "-n #name#";
	public final static String[] HELP_CONFIGURE = new String[] { "Configuration audio", };

	public int cmdConfigure(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("-n")).trim();
		try {
			_peer.configure(name);
		} catch (Exception e) {
			out.print(e.toString());
			_log.error(e.toString());
		}
		return 0;
	}

	// Volume
	public final static String USAGE_VOLUME = "-v #volume#";
	public final static String[] HELP_VOLUME = new String[] { "Volume audio", };

	public int cmdVolume(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			short volume = Short.parseShort(((String) opts.get("-v")).trim());
			_peer.setMasterVolume(volume);
		} catch (NumberFormatException e) {
			out.print(e.toString());
		}

		return 0;
	}
}
