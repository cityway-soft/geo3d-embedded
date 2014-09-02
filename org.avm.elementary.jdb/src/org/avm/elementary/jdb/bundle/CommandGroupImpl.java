package org.avm.elementary.jdb.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.impl.JDBConfig;
import org.avm.elementary.jdb.impl.JDBImpl;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "jdb";

	private JDBImpl _peer;

	CommandGroupImpl(ComponentContext context, JDB peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the jdb.");
		_peer = (JDBImpl) peer;
	}

	// filename
	public final static String USAGE_SETFILENAME = "<filename>";

	public final static String[] HELP_SETFILENAME = new String[] { "Set filename", };

	public int cmdSetfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String filename = ((String) opts.get("filename")).trim();
		((JDBConfig) _config).setFilename(filename);
		_config.updateConfig();

		out
				.println("Current filename : "
						+ ((JDBConfig) _config).getFilename());
		return 0;
	}

	//showfilename
	public final static String USAGE_SHOWFILENAME = "";

	public final static String[] HELP_SHOWFILENAME = new String[] { "Show current filename", };

	public int cmdShowfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out
				.println("Current filename : "
						+ ((JDBConfig) _config).getFilename());
		return 0;
	}

	// pattern
	public final static String USAGE_SETPATTERN = "<pattern>";

	public final static String[] HELP_SETPATTERN = new String[] { "Set pattern", };

	public int cmdSetpattern(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String pattern = ((String) opts.get("pattern")).trim();
		((JDBConfig) _config).setPattern(pattern);
		_config.updateConfig();

		out.println("Current pattern : " + ((JDBConfig) _config).getPattern());
		return 0;
	}

	public final static String USAGE_SHOWPATTERN = "";

	public final static String[] HELP_SHOWPATTERN = new String[] { "Show current pattern", };

	public int cmdShowpattern(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current pattern : " + ((JDBConfig) _config).getPattern());
		return 0;
	}

	// size
	public final static String USAGE_SETSIZE = "<size>";

	public final static String[] HELP_SETSIZE = new String[] { "Set buffer size", };

	public int cmdSetsize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String size = ((String) opts.get("size")).trim();
		((JDBConfig) _config).setSize(Integer.parseInt(size));
		_config.updateConfig();

		out.println("Current buffer size : " + ((JDBConfig) _config).getSize());
		return 0;
	}

	public final static String USAGE_SHOWSIZE = "";

	public final static String[] HELP_SHOWSIZE = new String[] { "Show current buffer size", };

	public int cmdShowsize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current buffer size : " + ((JDBConfig) _config).getSize());
		return 0;
	}

	// journalize
	public final static String USAGE_JOURNALIZE = "<message>";

	public final static String[] HELP_JOURNALIZE = new String[] { "Journalize", };

	public int cmdJournalize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String message = ((String) opts.get("message")).trim();
		_peer.journalize("console", message);
		return 0;
	}

	// sync (deprecated)
	public final static String USAGE_SYNC = "";

	public final static String[] HELP_SYNC = new String[] { "Synchronize buffer (deprecated)", };

	public int cmdSync(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_peer.sync();
		return 0;
	}
	
	// Flush
	public final static String USAGE_FLUSH = "";

	public final static String[] HELP_FLUSH = new String[] { "Flush buffer", };

	public int cmdFlush(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_peer.sync();
		return 0;
	}


}
