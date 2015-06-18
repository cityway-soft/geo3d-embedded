package org.avm.elementary.common;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.knopflerfish.service.console.CommandGroup;
import org.knopflerfish.service.console.CommandGroupAdapter;
import org.knopflerfish.service.console.Session;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

public abstract class AbstractCommandGroup extends CommandGroupAdapter
		implements ManageableService {

	protected Logger _log;

	protected ComponentContext _context;

	private ServiceRegistration _commandGroupRegistration;

	protected AbstractConfig _config;

	private String format = "text";

	public AbstractCommandGroup(ComponentContext context,
			AbstractConfig config, String groupName, String shortHelp) {
		super(groupName, shortHelp);
		_log = Logger.getInstance(this.getClass());
		_context = context;
		_config = config;
	}

	public void start() {
		Hashtable properties = new Hashtable();
		properties.put("groupName", getGroupName());
		_commandGroupRegistration = _context
				.getBundleContext()
				.registerService(CommandGroup.class.getName(), this, properties);
	}

	public void stop() {
		_commandGroupRegistration.unregister();
	}

	protected String getCategory() {
		String result = this.getClass().getPackage().getName();
		int index = result.lastIndexOf(".bundle");
		if (index > 0)
			result = result.substring(0, index);

		return result;
	}

	public String getFormat() {
		return format;
	}

	// output format
	public final static String USAGE_OUTPUT = "<format>";

	public final static String[] HELP_OUTPUT = new String[] { "Set log level", };

	public int cmdOutput(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		format = ((String) opts.get("format")).trim();
		
		session.getProperties().put("output-format", format);
		return 0;
	}

	// log level
	public final static String USAGE_SETLEVEL = "<level> [<category>]";

	public final static String[] HELP_SETLEVEL = new String[] { "Set log level", };

	public int cmdSetlevel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String level = ((String) opts.get("level")).trim();
		String category = (String) opts.get("category");
		category = (category != null) ? category : getCategory();
		Logger _log = Logger.getInstance(category);
		_log.setPriority(Priority.toPriority(level, Priority.DEBUG));
		out.println("Current log level : " + _log.getPriority()
				+ " for category " + category);
		return 0;
	}

	public final static String USAGE_SHOWLEVEL = "";

	public final static String[] HELP_SHOWLEVEL = new String[] { "Show current log level." };

	public int cmdShowlevel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Logger _log = Logger.getInstance(getCategory());
		out.println("Current log level : " + _log.getPriority());
		return 0;
	}

	// Delete
	public final static String USAGE_DELETE = "";

	public final static String[] HELP_DELETE = new String[] { "Delete configuration", };

	public int cmdDelete(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_config.delete();
		return 0;
	}

	// Update config
	public final static String USAGE_UPDATECONFIG = "";

	public final static String[] HELP_UPDATECONFIG = new String[] { "update configuration", };

	public int cmdUpdateconfig(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		_config.updateConfig();
		return 0;
	}

	public final static String USAGE_SHOWCONFIG = "";

	public final static String[] HELP_SHOWCONFIG = new String[] { "Show current config." };

	public int cmdShowconfig(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(_config);
		return 0;
	}

}
