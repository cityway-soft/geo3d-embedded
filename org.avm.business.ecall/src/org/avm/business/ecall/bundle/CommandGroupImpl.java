package org.avm.business.ecall.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.business.ecall.EcallService;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "ecall";

	private EcallService _peer;

	public CommandGroupImpl(ComponentContext context, EcallService peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for ecall.");
		_peer = peer;
	}

	public final static String USAGE_TRANSITION = "<transition> [<param>]";

	public final static String[] HELP_TRANSITION = new String[] {
			"Activation des transitions",
			"\tListe des transitions:\n" + "\t\t- alerte \n"
					+ "\t\t- priseencharge\n" + "\t\t- finalerte\n" };

	public int cmdTransition(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String transition = ((String) opts.get("transition")).trim();
		transition = transition.toLowerCase();

		if ("alerte".startsWith(transition)) {
			_peer.startEcall();
		} else if ("priseencharge".startsWith(transition)) {
			String phone = ((String) opts.get("param"));
			if (phone != null && !phone.equals("")) {
				_peer.ack(phone);
			} else {
				out.println("Usage : transition priseencharge <phone number>");
			}
		} else if ("finalerte".startsWith(transition)) {
			_peer.endEcall();
		} else {
			out.println("UNKNOWN Transition: " + transition);
		}
		out.println("State : " + _peer.getState());

		return 0;
	}

	public final static String USAGE_STATE = "";

	public final static String[] HELP_STATE = new String[] { "Activation des transitions" };

	public int cmdState(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		State state = _peer.getState();
		out.println("Current Ecall state : " + state);

		return 0;
	}

}
