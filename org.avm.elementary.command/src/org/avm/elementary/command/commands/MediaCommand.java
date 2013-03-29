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

	/** Fabrique de classe */
	public static class DefaultCommandFactory extends CommandFactory {

		protected Command createCommand() {

			return new MediaCommand();
		}
	}

	private final Logger _log = Activator.getDefault().getLogger();
	/** Referencement de la fabrique de classe */
	static {
		final CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(MediaCommand.class.getName(), factory);
	}

	public boolean execute(final Context o) {

		final MessengerContext context = (MessengerContext) o;
		if (context == null) {
			return true;
		}
		final Dictionary header = context.getHeader();
		if (header == null) {
			return true;
		}
		final Object message = context.getMessage();
		return this.process(context, header, message);
	}

	private boolean process(final MessengerContext context,
			final Dictionary header, final Object message) {

		final Map medias = context.getMedias();
		String media = null;
		if (medias != null) {
			for (final Iterator iterator = medias.keySet().iterator(); iterator
					.hasNext();) {
				final String key = (String) iterator.next();
				if (_log.isDebugEnabled()) {
					_log.debug("key=" + key);
				}
				if (key.startsWith("AVM") || key.startsWith("CTW")) {
					media = key;
				} else if ((media == null) && key.startsWith("SMS")) {
					if (message instanceof Message) {
						final Message msg = (Message) message;
						if ((msg.getEntete().getType() == 3 /* alerte */)
								|| (msg.getEntete().getType() == 2 /*
																	 * reponse
																	 * statut
																	 */)) {
							media = key;
						}
					}
				}
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("media selected =" + media);
		}
		if (media != null) {
			if (media.startsWith("AVM") || media.startsWith("CTW")) {
				header.put("MEDIA_ID", media);
				return false;
			} else if (media.startsWith("SMS")) {
				if (message instanceof Message) {
					final Message msg = (Message) message;
					if ((msg.getEntete().getType() == 3)
							|| (msg.getEntete().getType() == 2)) {
						header.put("MEDIA_ID", media);
						final String id = System
								.getProperty("org.avm.server.sms");
						header.put("binary", "true");
						header.put("id", id);
						return false;
					}
				}
			}
		}
		return true;
	}
}
