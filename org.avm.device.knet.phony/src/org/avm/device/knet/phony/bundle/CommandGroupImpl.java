package org.avm.device.knet.phony.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;

import org.avm.device.knet.phony.PhonyConfig;
import org.avm.device.knet.phony.PhonyImpl;
import org.avm.device.phony.Phony;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String CATEGORY = "org.avm.device.phony";

	public static final String COMMAND_GROUP = "knet.phony";

	private Phony _peer;

	private ConfigImpl _config;

	public CommandGroupImpl(ComponentContext context, Phony peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for phony.");
		_peer = peer;
		_config = config;
	}

	// Call
	public final static String USAGE_CALL = "<name>";

	public final static String[] HELP_CALL = new String[] { "Call name" };

	public int cmdCall(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("name")).trim();
		try {
			_peer.call(name);
		} catch (Exception e) {
			out.println("GsmException : " + e.getMessage());
		}
		return 0;
	}

	// Dial
	public final static String USAGE_DIAL = "<number>";

	public final static String[] HELP_DIAL = new String[] { "Dial phone number" };

	public int cmdDial(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String number = ((String) opts.get("number")).trim();
		try {
			_peer.dial(number);
		} catch (Exception e) {
			out.println("Exception : " + e.getMessage());
		}
		return 0;
	}

	// Hang-up
	public final static String USAGE_HANGUP = "";

	public final static String[] HELP_HANGUP = new String[] { "Hang-up" };

	public int cmdHangup(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			_peer.hangup();
		} catch (Exception e) {
			out.println("GsmException : " + e.getMessage());
		}
		return 0;
	}

	// Answer
	public final static String USAGE_ANSWER = "";

	public final static String[] HELP_ANSWER = new String[] { "Answer to the incomming call" };

	public int cmdAnswer(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			_peer.answer();
		} catch (Exception e) {
			out.println("GsmException : " + e.getMessage());
		}
		return 0;
	}

	private String createList() {
		Enumeration e = _peer.getContactList();
		StringBuffer buffer = new StringBuffer();
		if (e != null) {
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				buffer.append(name);
				buffer.append("=");
				buffer.append(_peer.getPhoneNumber(name));
				if (e.hasMoreElements()) {
					buffer.append(";");
				}
			}
		}
		return buffer.toString();
	}

	// Get number
	public final static String USAGE_GETNUMBER = "<name>";

	public final static String[] HELP_GETNUMBER = new String[] { "Get a phone number from name" };

	public int cmdGetnumber(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("name")).trim();
		String phonenumber = _peer.getPhoneNumber(name);
		if (phonenumber != null) {
			out.println(name + "'s phone number is " + phonenumber);
		} else {
			out.println(name + " has no phonenumber");
		}
		return 0;
	}

	// Get list
	public final static String USAGE_GETLIST = "";

	public final static String[] HELP_GETLIST = new String[] { "Get contact list" };

	public int cmdGetlist(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("DEBUG : " + ((PhonyConfig) _config).getContactList());
		Enumeration e = _peer.getContactList();
		if (e != null) {
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				String phone = _peer.getPhoneNumber(name);
				out.println(name + ": " + phone);

			}
		} else {
			out.println("Contact list empty");
		}
		return 0;
	}

	// Get list
	public final static String USAGE_RING = "";

	public final static String[] HELP_RING = new String[] { "Simule un ring" };

	public int cmdRing(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		((PhonyImpl) _peer).ringing("test");
		return 0;
	}

}
