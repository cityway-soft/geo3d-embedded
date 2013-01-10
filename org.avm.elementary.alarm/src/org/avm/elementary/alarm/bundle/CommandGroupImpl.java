package org.avm.elementary.alarm.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Properties;

import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.alarm.impl.AlarmServiceConfig;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "alarm";

	private AlarmService _peer;

	CommandGroupImpl(ComponentContext context, AlarmService peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for alarm service.");
		_peer = peer;

	}

	// Add
	public final static String USAGE_ADD = "-i #index# -s #source#  -n #notify# -a #acknowledge#";

	public final static String[] HELP_ADD = new String[] { "Add alarm index", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		Integer index = Integer.valueOf((String) opts.get("-i"));
		String source = ((String) opts.get("-s")).trim();
		Boolean notify = Boolean.valueOf((String) opts.get("-n"));
		Boolean acknowledge = Boolean.valueOf((String) opts.get("-a"));

		Properties p = new Properties();
		p.put(Alarm.INDEX, index.toString());
		p.put(Alarm.SOURCE, source.toString());
		p.put(Alarm.NOTIFY, notify.toString());
		p.put(Alarm.ACKNOWLEDGE, acknowledge.toString());

		((AlarmServiceConfig) _config).add(p);

		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-i #index#";

	public final static String[] HELP_REMOVE = new String[] { "Remove alarm index", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		int index = Integer.parseInt(((String) opts.get("-i")).trim());
		((AlarmServiceConfig) _config).remove("" + index);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all alarm index", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(_config.toString());
		return 0;
	}

}
