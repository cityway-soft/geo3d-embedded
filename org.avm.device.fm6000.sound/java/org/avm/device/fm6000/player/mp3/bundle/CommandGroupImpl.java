package org.avm.device.fm6000.player.mp3.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.fm6000.player.mp3.Mp3Player;
import org.avm.device.player.Player;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "mp3";
	private Player _peer;

	CommandGroupImpl(ComponentContext context, Player peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the player.");
		_peer = peer;
	}

	// play
	public final static String USAGE_PLAY = "<name>";
	public final static String[] HELP_PLAY = new String[] { "Play sound file", };

	public int cmdPlay(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("name")).trim();
		try {
			_peer.open(name);
			_peer.play();
			_peer.close();
		} catch (RuntimeException e) {
			_log.error(e.getMessage());
		}
		

		return 0;
	}

	// stop
	public final static String USAGE_STOP = "";
	public final static String[] HELP_STOP = new String[] { "Stop player", };

	public int cmdStop(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		_peer.stop();

		return 0;
	}
}
