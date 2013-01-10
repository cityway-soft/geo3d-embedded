package org.avm.elementary.rsync.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.rsync.RSync;
import org.avm.elementary.rsync.impl.RSyncImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "rsync";

	private RSyncImpl _peer;

	CommandGroupImpl(ComponentContext context, RSync peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the rsync service.");
		_peer = (RSyncImpl) peer;
	}

	// rsync
	public final static String USAGE_RSYNC = "[<args>] ...";

	public final static String[] HELP_RSYNC = new String[] { "type rsync --help" };

	public int cmdRsync(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String[] args = (String[]) opts.get("args");
		String[] argv = new String[args.length + 1];
		argv[0] = "rsync";
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				argv[i + 1] = "-" + args[i];
			} else {
				argv[i + 1] = args[i];
			}
		}

		_peer.rsync(argv);
		return 0;
	}
}
