package org.avm.elementary.management.addons;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.osgi.framework.BundleContext;

public interface Command {
	public String getName();

	/**
	 * @param context
	 * @param parameters
	 * @param out
	 * @param management
	 * @throws CommandException
	 * @throws IOException
	 */
	public void execute(BundleContext context, Properties parameters,
			PrintWriter out, ManagementService management)
			throws CommandException, IOException;

	public static final String ERR_CMD_NOT_FOUND = "cmd not found";
	public static final String ERR_SYNTAX = "syntax err";
	public static final String ERR_RUNTIME = "runtime err";
	public static final String OK = "ok";
}
