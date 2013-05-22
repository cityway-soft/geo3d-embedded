package org.avm.elementary.can.logger.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.elementary.can.logger.Logger;
import org.avm.elementary.can.logger.LoggerConfig;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	private Logger _peer;

	public static final String COMMAND_GROUP = "can.logger";

	CommandGroupImpl(ComponentContext context, Logger peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the can looger.");
		_peer = peer;
	}

	// can service pid
	public final static String USAGE_SETCANSERVICEPID = "<pid>";

	public final static String[] HELP_SETCANSERVICEPID = new String[] { "Set can service pid", };

	public int cmdSetcanservicepid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String pid = (String) opts.get("pid");
		((LoggerConfig) _config).setCanServicePid(pid);
		_config.updateConfig();

		out.println("Current can service pid : "
				+ ((LoggerConfig) _config).getCanServicePid());
		return 0;
	}

	public final static String USAGE_SHOWCANSERVICEPID = "";

	public final static String[] HELP_SHOWCANSERVICEPID = new String[] { "Show current can service pid", };

	public int cmdShowcanservicepid(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current can service pid : "
				+ ((LoggerConfig) _config).getCanServicePid());
		return 0;
	}

	// filename
	public final static String USAGE_SETFILENAME = "<filename>";

	public final static String[] HELP_SETFILENAME = new String[] { "Set filename", };

	public int cmdSetfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String filename = ((String) opts.get("filename")).trim();
		((LoggerConfig) _config).setFilename(filename);
		_config.updateConfig();

		out.println("Current filename : "
				+ ((LoggerConfig) _config).getFilename());
		return 0;
	}

	public final static String USAGE_SHOWFILENAME = "";

	public final static String[] HELP_SHOWFILENAME = new String[] { "Show current filename", };

	public int cmdShowfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current filename : "
				+ ((LoggerConfig) _config).getFilename());
		return 0;
	}

}
