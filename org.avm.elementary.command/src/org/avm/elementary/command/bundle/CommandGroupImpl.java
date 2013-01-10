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

	private CommandChain _peer;

	CommandGroupImpl(ComponentContext context, CommandChain peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the command chain.");
		_peer = peer;
	}

	// test
	public final static String USAGE_TEST = "";

	public final static String[] HELP_TEST = new String[] { "Test command chain", };

	public int cmdTest(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		ReponseStatut o = new ReponseStatut();
		o.getEntete().getChamps().setPosition(1);
		try {
			MessengerContext context = new MessengerContext();
			context.setComponentContext(_context);
			context.setMessage(o);
			out
					.println("Test command chain result : "
							+ _peer.execute(context));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
