package org.avm.elementary.messenger.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.common.Media;
import org.avm.elementary.messenger.impl.MessengerImpl;
import org.avm.elementary.parser.Parser;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "messenger";

	private MessengerImpl _peer;

	CommandGroupImpl(ComponentContext context, MessengerImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the gps.");
		_peer = peer;
	}

	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "(DEBUG) list register parsers and medias" };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		Hashtable parsers = ((MessengerImpl) _peer).getParsers();
		out.println("Parsers:");
		if (parsers != null) {
			for (Enumeration iter = parsers.elements(); iter.hasMoreElements();) {
				Parser parser = (Parser) iter.nextElement();
				out.println("-" + parser.getProtocolName());
			}
		} else {
			out.println("none");
		}

		Hashtable medias = ((MessengerImpl) _peer).getMedias();
		out.println("Medias:");
		if (parsers != null) {
			for (Enumeration iter = medias.elements(); iter.hasMoreElements();) {
				Media media = (Media) iter.nextElement();
				out.println("-" + media.getMediaId());
			}
		} else {
			out.println("none");
		}

		return 0;
	}

}
