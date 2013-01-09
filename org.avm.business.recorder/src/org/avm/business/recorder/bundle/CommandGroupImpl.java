package org.avm.business.recorder.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.business.recorder.Recorder;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String CATEGORY = "org.avm.business.eventRecorder";

	public static final String COMMAND_GROUP = "recorder";

	private ConfigImpl _config;

	private Recorder _peer;

	CommandGroupImpl(ComponentContext context, Recorder peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for 'Event Recorder'.");
		_peer = peer;
		_config = config;
	}

	public final static String USAGE_JDBLIGHT = "";

	public final static String[] HELP_JDBLIGHT = new String[] { "Recuperation du jdb ligth" };

	public int cmdJdblight(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		_peer.syncJdbLight();
		return 0;
	}

}
