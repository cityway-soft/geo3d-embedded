
package org.avm.elementary.command.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.avm.elementary.command.MessengerContext;
import org.avm.elementary.command.impl.CommandFactory;

public class IdentiteCommand implements org.apache.commons.chain.Command {
	
	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {
		
		protected Command createCommand() {
		
			return new IdentiteCommand();
		}
	}
	
	private static final String VEHICULE_ID_KEY     = "org.avm.terminal.name";
	
	private static final String EXPLOITATION_ID_KEY = "org.avm.terminal.owner";
	
	private MessengerContext    _context;
	/** Referencement de la fabrique de classe */
	static {
		final CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(IdentiteCommand.class.getName(), factory);
	}
	
	public boolean execute(final Context context) throws Exception {
	
		//
		// this._context = (MessengerContext) context;
		// if (this._context == null) { return true; }
		// final Object msg = this._context.getMessage();
		// if (msg instanceof Message) {
		// final Message message = (Message) msg;
		// if (message == null) { return true; }
		// final Entete entete = message.getEntete();
		// if (entete.getSource() != null) {
		// final Identite source = message.getEntete().getSource();
		// source.setExploitant(this.getIntProperty(IdentiteCommand.EXPLOITATION_ID_KEY));
		// source.setType(0);
		// source.setId(this.getIntProperty(IdentiteCommand.VEHICULE_ID_KEY));
		// }
		// if (entete.getDestination() != null) {
		// final Identite destination = message.getEntete().getDestination();
		// destination.setExploitant(this.getIntProperty(IdentiteCommand.EXPLOITATION_ID_KEY));
		// destination.setType(0);
		// destination.setId(0);
		// }
		// // MESSAGE_DEST_ID
		// final DecimalFormat form = new DecimalFormat("000");
		// final String id = form.format(entete.getDestination().getExploitant());
		// this._context.getHeader().put("MESSAGE_DEST_ID", id);
		// }
		return false;
	}
	
	private int getIntProperty(final String key) {
	
		int result = 0;
		try {
			result = Integer.parseInt(System.getProperty(key, "0"));
		}
		catch (final NumberFormatException nef) {
		}
		return result;
	}
}
