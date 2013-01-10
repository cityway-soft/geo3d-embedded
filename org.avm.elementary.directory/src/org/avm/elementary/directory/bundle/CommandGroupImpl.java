package org.avm.elementary.directory.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Properties;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.directory.Directory;
import org.avm.elementary.directory.impl.DirectoryConfig;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "directory";

	private final Directory _peer;

	CommandGroupImpl(final ComponentContext context, final Directory peer,
			final ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the directory.");
		_peer = peer;
	}

	//Filename
	public final static String USAGE_SETFILENAME = "<path>";
	public final static String[] HELP_SETFILENAME = new String[] { "Set data path", };

	public int cmdSetfilename(final Dictionary opts, final Reader in,
			final PrintWriter out, final Session session) {
		final String path = (String) opts.get("path");
		((DirectoryConfig) _config).setFileName(path);
		_config.updateConfig();
		out.println("Current data path : "
				+ ((DirectoryConfig) _config).getFileName());
		return 0;
	}

	public final static String USAGE_SHOWFILENAME = "";
	public final static String[] HELP_SHOWFILENAME = new String[] { "Show current data path", };

	public int cmdShowfilename(final Dictionary opts, final Reader in,
			final PrintWriter out, final Session session) {
		out.println("Current data path : "
				+ ((DirectoryConfig) _config).getFileName());
		return 0;
	}

	// Add
	public final static String USAGE_ADD = "-n #name# [<args>] ...";

	public final static String[] HELP_ADD = new String[] { "Add properties", };

	public int cmdAdd(final Dictionary opts, final Reader in,
			final PrintWriter out, final Session session) {

		final String name = ((String) opts.get("-n")).trim();
		final String[] args = (String[]) opts.get("args");

		final Properties properties = new Properties();
		properties.put(Directory.KEY, name);
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				final String text = args[i];
				final int n = text.indexOf('=');
				if (n != -1) {
					final String key = text.substring(0, n);
					final String value = text.substring(n + 1);
					properties.put(key, value);
				}
			}
		}
		final boolean ret = ((DirectoryConfig) _config).addProperty(name,
				properties);
		out.println("Property added : " + ret);
		_config.updateConfig(false);
		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-n #name#";

	public final static String[] HELP_REMOVE = new String[] { "Remove properties", };

	public int cmdRemove(final Dictionary opts, final Reader in,
			final PrintWriter out, final Session session) {
		final String name = ((String) opts.get("-n")).trim();
		((DirectoryConfig) _config).removeProperty(name);
		_config.updateConfig(false);
		return 0;
	}

	// Loadproperties
	public final static String USAGE_LOADPROPERTIES = "[<filename>]";

	public final static String[] HELP_LOADPROPERTIES = new String[] { "Load properties", };

	public int cmdLoadproperties(final Dictionary opts, final Reader in,
			final PrintWriter out, final Session session) {
		final String filename = (String) opts.get("filename");
		final boolean ret = ((DirectoryConfig) _config)
				.loadProperties(filename);
		out.println("Properties loaded :" + ret);
		return 0;
	}

	// Saveproperties
	public final static String USAGE_SAVEPROPERTIES = "[<filename>]";

	public final static String[] HELP_SAVEPROPERTIES = new String[] { "Save properties", };

	public int cmdSaveproperties(final Dictionary opts, final Reader in,
			final PrintWriter out, final Session session) {
		final String filename = (String) opts.get("filename");
		final boolean ret = ((DirectoryConfig) _config)
				.saveProperties(filename);
		out.println("Properties saved: " + ret);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all properties", };

	public int cmdList(final Dictionary opts, final Reader in,
			final PrintWriter out, final Session session) {
		out.println(((DirectoryConfig) _config).getProperty(null));
		return 0;
	}

}
