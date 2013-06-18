package org.avm.business.messages.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.business.messages.Messages;
import org.avm.business.messages.MessagesConfig;
import org.avm.business.messages.MessagesImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String CATEGORY = "org.avm.business.messages";

	public static final String COMMAND_GROUP = "messages";

	private ConfigImpl _config;

	private MessagesImpl _peer;

	CommandGroupImpl(ComponentContext context, Messages peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for 'messages'.");
		_peer = (MessagesImpl) peer;
		_config = config;
	}

	// Add message
	public final static String USAGE_ADDMESSAGE = "[-l #affectation#] [-p #priorite#] [-a #acquittement#] [-t #type#] [-d #debut#] [-f #fin#] [-j #jourssemaine#] <id> <message> ";

	public final static String[] HELP_ADDMESSAGE = new String[] {
			"Add message", "-t <0|1> 0=conducteur, 1=voyageur" };

	public int cmdAddmessage(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String id = ((String) opts.get("id")).trim();
		String message = ((String) opts.get("message")).trim();

		String tmp = ((String) opts.get("-t"));
		int type = (tmp != null) ? Integer.parseInt(tmp) : Messages.VOYAGEUR;

		String affectation = ((String) opts.get("-l"));
		boolean acquittement = ((String) opts.get("-a")) != null;
		tmp = ((String) opts.get("-p"));
		int priorite = (tmp != null) ? Integer.parseInt(tmp) : 0;

		String debut = ((String) opts.get("-d"));
		String fin = ((String) opts.get("-f"));
		String jours = ((String) opts.get("-j"));
		
		if (type == Messages.CONDUCTEUR){
			fin = null;
		}
		
		addMessage(id, debut, fin, jours, type, affectation, message, priorite,
				acquittement);
		return 0;
	}

	// Add driver message
	public final static String USAGE_ADDDRIVERMESSAGE = "[-p #priorite#] [-a #acquittement#] <id> <message> ";

	public final static String[] HELP_ADDDRIVERMESSAGE = new String[] {
			"Add driver message", "" };

	public int cmdAdddrivermessage(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String id = ((String) opts.get("id")).trim();
		String message = ((String) opts.get("message")).trim();

		int type = Messages.CONDUCTEUR;

		String affectation = null;
		boolean acquittement = ((String) opts.get("-a")) != null;
		String tmp = ((String) opts.get("-p"));
		int priorite = (tmp != null) ? Integer.parseInt(tmp) : 0;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date ddebut = cal.getTime();

		String debut = Messages.DF.format(ddebut);
		String fin = null;
		String jours = "LMMJVSD";

		addMessage(id, debut, fin, jours, type, affectation, message, priorite,
				acquittement);
		return 0;
	}

	// Add travelers message
	public final static String USAGE_ADDTRAVELERSMESSAGE = "[-l #affectation#] [-p #priorite#] [-a #acquittement#] [-d #debut#] [-f #fin#] [-j #jourssemaine#] <id> <message> ";

	public final static String[] HELP_ADDTRAVELERSMESSAGE = new String[] {
			"Add message", "-t <0|1> 0=conducteur, 1=voyageur" };

	public int cmdAddtravelersmessage(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String id = ((String) opts.get("id")).trim();
		String message = ((String) opts.get("message")).trim();

		int type = Messages.VOYAGEUR;

		String affectation = ((String) opts.get("-l"));
		boolean acquittement = ((String) opts.get("-a")) != null;
		String tmp = ((String) opts.get("-p"));
		int priorite = (tmp != null) ? Integer.parseInt(tmp) : 0;

		String debut = ((String) opts.get("-d"));
		String fin = ((String) opts.get("-f"));
		String jours = ((String) opts.get("-j"));
		addMessage(id, debut, fin, jours, type, affectation, message, priorite,
				acquittement);
		return 0;
	}

	public void addMessage(String id, String debut, String fin, String jours,
			int type, String affectation, String message, int priorite,
			boolean acquittement) {

		Date dateReception = new Date();
		String reception = Messages.DF.format(dateReception);
		_log.debug("Date object=" + dateReception + ", formatted=" + reception);

		
		((MessagesConfig) _config).addMessage(id, reception, debut, fin, jours, type,
				affectation, message, priorite, acquittement);
		_config.updateConfig(false);
		_peer.configure(_config);
		_peer.publish(type);
	}

	// Remove
	public final static String USAGE_REMOVEMESSAGE = "<id>";

	public final static String[] HELP_REMOVEMESSAGE = new String[] { "Remove properties", };

	public int cmdRemovemessage(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String id = ((String) opts.get("id")).trim();
		((MessagesConfig) _config).removeMessage(id);
		_config.updateConfig(false);
		_peer.configure(_config);
		_peer.publish(Messages.VOYAGEUR);
		return 0;
	}

	// Remove
	public final static String USAGE_CLEARMESSAGES = "";

	public final static String[] HELP_CLEARMESSAGES = new String[] { "Remove all messages", };

	public int cmdClearmessages(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		((MessagesConfig) _config).removeAllMessages();
		_config.updateConfig(false);
		_peer.configure(_config);
		_peer.publish(Messages.VOYAGEUR);
		return 0;
	}

	// List
	public final static String USAGE_LISTMESSAGES = "";

	public final static String[] HELP_LISTMESSAGES = new String[] { "List all messages", };

	public int cmdListmessages(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Dictionary dic = ((MessagesConfig) _config).getMessages();
		Enumeration e = dic.keys();
		StringBuffer buf = new StringBuffer();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			
			if (key.length()<9){
				buf.append(" ");
			}
			buf.append(key);
			buf.append(" ");
			Properties p = (Properties) dic.get(key);
			int type = Integer.parseInt(p.getProperty(Messages.TYPE));
			String dest = type == Messages.CONDUCTEUR ? "DRIVER"
					: "TRAVEL";
			buf.append("[");
			buf.append(dest);
			buf.append("]");
			buf.append(":");
			
			buf.append(" (");
			
			if (type == Messages.VOYAGEUR) {
				if (((MessagesImpl) _peer).isValid(p)) {
					boolean isJour = ((MessagesImpl) _peer).isJour(p);
					boolean isHeure = ((MessagesImpl) _peer).isHeure(p);
					if (isJour && isHeure) {
						buf.append("         Valide         ");
					} else {
						if (!isJour) {
							buf.append("Invalide / Jour semaine ");
						} else {
							buf.append("Invalide / Plage horaire");
						}
					}
				} else {
					buf.append("   Invalide / Periode   ");
				}
			} else {
				buf.append("          - - -         ");
			}
			buf.append(") ");
			buf.append(" { ");
			if (type == Messages.VOYAGEUR) {
				buf.append("debut:");
				buf.append(p.getProperty(Messages.DEBUT));
				buf.append(" fin:");
				buf.append(p.getProperty(Messages.FIN));
				buf.append(" jours:");
				buf.append(p.getProperty(Messages.JOURSEMAINE));
				buf.append(" affec:");
				buf.append(p.getProperty(Messages.AFFECTATION));
				buf.append(" prio:");
				buf.append(p.getProperty(Messages.PRIORITE));
				buf.append(" msg:");
				buf.append(p.getProperty(Messages.MESSAGE));
			} else {
				buf.append("recu:");
				buf.append(p.getProperty(Messages.DEBUT));
				buf.append(" lu:");
				buf.append(p.getProperty(Messages.FIN));
				buf.append(" acq:");
				buf.append(p.getProperty(Messages.ACQUITTEMENT));
				buf.append(" prio:");
				buf.append(p.getProperty(Messages.PRIORITE));
				buf.append(" msg:");
				buf.append(p.getProperty(Messages.MESSAGE));
			}
			buf.append(" } ");
			buf.append("\n\n");
		}
		out.println(buf);
		out.println();
		return 0;
	}

}
