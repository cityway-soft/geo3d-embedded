package org.avm.elementary.media.sms.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Properties;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.media.sms.MediaSMS;
import org.avm.elementary.media.sms.MediaSMSConfig;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "sms";

	private MediaSMS _peer;

	public CommandGroupImpl(ComponentContext context, MediaSMS peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP, "Configuration commands for sms.");
		_peer = peer;
	}

	public final static String USAGE_SETSMSCENTER = "[<smscenter>]";

	public final static String[] HELP_SETSMSCENTER = new String[] { "Set sms center number.\r"
			+ "\tSFR     : 0609001390\r"
			+ "\tOrange  : 0689004000\r"
			+ "\tBouygues: 0660003000\r" };

	public int cmdSetsmscenter(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String smscenter = ((String) opts.get("smscenter"));
		if (smscenter != null) {
			((MediaSMSConfig) _config).setSMSCenter(smscenter);
			_config.updateConfig(true);
		}
		out.println("SMSCenter:" + ((MediaSMSConfig) _config).getSMSCenter());

		return 0;
	}

	public final static String USAGE_SENDBINARY = "<msg> <number>";

	public final static String[] HELP_SENDBINARY = new String[] { "Send a binary sms to a destinary.\r"
			+ "\t<msg>: character equivallent byte\r"
			+ "\t<number>: destinary phone number\r"
			+ "\texample: sendbinary hello 0688964207" };

	public int cmdSendbinary(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String msg = ((String) opts.get("msg")).trim();
		String num = ((String) opts.get("number")).trim();

		try {
			Dictionary header = new Properties();
			header.put(MediaSMS.BINARY, "true");
			header.put(MediaSMS.MESSAGE_DEST_ID, num);
			_peer.send(header, msg.getBytes());
		} catch (Exception e) {
			out.println("GsmException : " + e.getMessage());
		}
		return 0;
	}

	public final static String USAGE_SENDTEXTO = "<msg> <number>";

	public final static String[] HELP_SENDTEXTO = new String[] { "Send a texto sms to a destinary.\r"
			+ "\t<msg>: string\r"
			+ "\t<number>: destinary phone number\r"
			+ "\texample: sendtexto hello 0688964207" };

	public int cmdSendtexto(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String msg = ((String) opts.get("msg")).trim();
		String num = ((String) opts.get("number")).trim();

		try {
			Dictionary header = new Properties();
			header.put("binary", "false");
			header.put("id", num);
			_peer.send(header, msg.getBytes());
		} catch (Exception e) {
			out.println("GsmException : " + e.getMessage());
		}
		return 0;
	}
	
	
	public final static String USAGE_SENDMSG = "[-m #m] [-d #d] [-t #t]";

	public final static String[] HELP_SENDMSG = new String[] { "Send sms to a destinary.\r"
			+ "\t-m : string\r"
			+ "\t-d: destinary phone number\r"
			+ "\t-t: 'texto' / 'binary'" };

	public int cmdSendmsg(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String msg = ((String) opts.get("-m")).trim();
		String num = ((String) opts.get("-d")).trim();
		String type = ((String) opts.get("-t"));

		try {
			Dictionary header = new Properties();
			header.put("binary", (type==null || !type.equals("binary"))?"false":"true");
			header.put("id", num);
			_peer.send(header, msg.getBytes());
		} catch (Exception e) {
			out.println("GsmException : " + e.getMessage());
		}
		return 0;
	}

}
