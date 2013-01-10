package org.avm.elementary.command.commands;

import java.text.DecimalFormat;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.Identite;
import org.avm.business.protocol.phoebus.Message;
import org.avm.elementary.command.MessengerContext;
import org.avm.elementary.command.impl.CommandFactory;

public class IdentiteCommand implements org.apache.commons.chain.Command {
	private static final String VEHICULE_ID_KEY = "org.avm.vehicule.id";
	private static final String EXPLOITATION_ID_KEY = "org.avm.exploitation.id";
	private MessengerContext _context;

	public boolean execute(Context context) throws Exception {
		_context = (MessengerContext) context;
		if (_context == null)
			return true;
		Message message = (Message) _context.getMessage();
		if (message == null)
			return true;

		Entete entete = message.getEntete();
		if (entete.getSource() != null) {
			Identite source = message.getEntete().getSource();
			source.setExploitant(getIntProperty(EXPLOITATION_ID_KEY));
			source.setType(0);
			source.setId(getIntProperty(VEHICULE_ID_KEY));
		}
		if (entete.getDestination() != null) {
			Identite destination = message.getEntete().getDestination();
			destination.setExploitant(getIntProperty(EXPLOITATION_ID_KEY));
			destination.setType(0);
			destination.setId(0);
		}

		// MESSAGE_DEST_ID
		DecimalFormat form = new DecimalFormat("000");
		String id = form.format(entete.getDestination().getExploitant());
		_context.getHeader().put("MESSAGE_DEST_ID", id);

		return false;
	}

	private int getIntProperty(String key) {
		int result = 0;
		try {
			result = Integer.parseInt(System.getProperty(key, "0"));
		} catch (NumberFormatException nef) {
		}
		return result;
	}

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {
		protected Command createCommand() {
			return new IdentiteCommand();
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CommandFactory factory = new DefaultCommandFactory();
		DefaultCommandFactory.factories.put(IdentiteCommand.class.getName(),
				factory);
	}
}
