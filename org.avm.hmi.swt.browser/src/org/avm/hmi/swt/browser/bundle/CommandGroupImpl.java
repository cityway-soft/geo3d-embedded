package org.avm.hmi.swt.browser.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.hmi.swt.browser.BrowserImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "swt.browser";

	private BrowserImpl _peer;

	CommandGroupImpl(ComponentContext context, BrowserImpl peer) {
		super(context, null, COMMAND_GROUP,
				"Configuration commands browser");
		_peer = peer;
	}

	// Setperiode
	public final static String USAGE_SETURL = "<url>";

	public final static String[] HELP_SETURL = new String[] { "Set url to browser", };

	public int cmdSeturl(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String url = (String) opts.get("url");
		try {
			_peer.setUrl(new URL(url));
		} catch (MalformedURLException e) {
			out.println("URL Malformée : " + e.getMessage());
		}

		return 0;
	}

}
