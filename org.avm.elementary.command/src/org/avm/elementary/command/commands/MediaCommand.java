package org.avm.elementary.command.commands;

import java.util.Dictionary;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.Message;
import org.avm.elementary.command.MessengerContext;
import org.avm.elementary.command.bundle.Activator;
import org.avm.elementary.command.impl.CommandFactory;

public class MediaCommand implements org.apache.commons.chain.Command {

	private Logger _log = Activator.getDefault().getLogger();

	public boolean execute(Context o) {
		MessengerContext context = (MessengerContext) o;
		if (context == null)
			return true;

		Message message = (Message) context.getMessage();
		if (message == null)
			return true;

		Dictionary header = (Dictionary) context.getHeader();
		if (header == null)
			return true;

		return process(context, header, message);
	}

	private boolean process(MessengerContext context, Dictionary header,
			Message message) {
		Map medias = context.getMedias();
		String media = null;
		if (medias != null) {
			for (Iterator iterator = medias.keySet().iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				if (key.startsWith("AVM")) {
					media = key;
				} else if (media == null
						&& key.startsWith("SMS")
						&& (message.getEntete().getType() == 3 /* alerte */ || message
								.getEntete().getType() == 2 /* reponse statut */)) {
					media = key;
				}
			}
		}
		
		
		if (media != null) {
			if (media.startsWith("AVM")) {
				header.put("MEDIA_ID", media);
				return false;
			} else if (media.startsWith("SMS")
					&& (message.getEntete().getType() == 3 || message
							.getEntete().getType() == 2)) {
				header.put("MEDIA_ID", media);
				String id = System.getProperty("org.avm.server.sms");
				header.put("binary", "true");
				header.put("id", id);
				return false;
			}
		}

		return true;
	}

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {
		protected Command createCommand() {
			return new MediaCommand();
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CommandFactory factory = new DefaultCommandFactory();
		DefaultCommandFactory.factories.put(MediaCommand.class.getName(),
				factory);
	}
}
