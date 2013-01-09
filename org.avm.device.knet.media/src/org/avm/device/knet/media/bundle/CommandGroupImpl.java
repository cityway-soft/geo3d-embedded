package org.avm.device.knet.media.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import org.avm.device.knet.media.MediaKnet;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String LOGGER = "org.avm.device.knet.media";
	public static final String COMMAND_GROUP = "knet.media";
	private ConfigImpl _config;
	private MediaKnet _peer;

	protected CommandGroupImpl(ComponentContext context, MediaKnet peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Commandes de configuration pour MediaKnet");
		_peer = peer;
		_config = (ConfigImpl) config;
	}

	public final static String USAGE_SENDMSG = "<msg><dest>";
	public final static String[] HELP_SENDMSG = new String[] { "envoie un message" };

	public int cmdSendmsg(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String msg = ((String) opts.get("msg")).trim();
		String dst = ((String) opts.get("dest")).trim();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHMMSS");
		String jour = sdf.format(new Date());
		String test = "[" + jour + "] " + msg;
		out.print("Envoie du message '" + test + "'");
		byte[] data = test.getBytes();
		Hashtable header = new Hashtable();
		header.put("id", dst);
		try {
			_peer.send(header, data);
			out.println(" OK");
		} catch (Exception e) {
			out.println(" NOK");
			out.println("\n" + e.getLocalizedMessage());
		}
		return 0;
	}

}
