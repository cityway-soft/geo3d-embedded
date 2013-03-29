package org.avm.elementary.command.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.business.protocol.phoebus.ReponseStatut;
import org.avm.elementary.command.CommandChain;
import org.avm.elementary.command.MessengerContext;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "command";

	private final CommandChain _peer;

	// test
	public final static String USAGE_TEST = "";

	public final static String[] HELP_TEST = new String[] { "Test command chain", };

	CommandGroupImpl(final ComponentContext context, final CommandChain peer,
			final ConfigImpl config) {

		super(context, config, CommandGroupImpl.COMMAND_GROUP,
				"Configuration commands for the command chain.");
		this._peer = peer;
	}

	public int cmdTest(final Dictionary opts, final Reader in,
			final PrintWriter out, final Session session) {

		final org.avm.business.protocol.phoebus.ReponseStatut o = new ReponseStatut();
		// o.getEntete().getChamps().setPosition(1);
		try {
			final MessengerContext context = new MessengerContext();
			context.setComponentContext(this._context);
			context.setMessage(o);
			out.println("Test command chain result : "
					+ this._peer.execute(context));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
