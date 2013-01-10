package org.avm.hmi.swt.alarm.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.hmi.swt.alarm.AlarmConfig;
import org.avm.hmi.swt.alarm.AlarmIhm;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "swt.alarm";

	private AlarmIhm _peer;

	CommandGroupImpl(ComponentContext context, AlarmIhm peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the HMI Alarm.");
		_peer = peer;
	}

//	// Add
//	public final static String USAGE_ADD = "-n #name# [<args>] ...";
//
//	public final static String[] HELP_ADD = new String[] { "Add properties", };
//
//	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
//			Session session) {
//
//		String name = ((String) opts.get("-n")).trim();
//		String[] args = (String[]) opts.get("args");
//
//		Properties properties = new Properties();
//		properties.put(Directory.KEY, name);
//		if (args != null) {
//			for (int i = 0; i < args.length; i++) {
//				String text = args[i];
//				int n = text.indexOf('=');
//				if (n != -1) {
//					String key = text.substring(0, n);
//					String value = text.substring(n + 1);
//					properties.put(key, value);
//				}
//			}
//		}
//		((DirectoryConfig) _config).addProperty(name, properties);
//		_config.updateConfig(false);
//		return 0;
//	}

//	// Remove
//	public final static String USAGE_REMOVE = "-n #name#";
//
//	public final static String[] HELP_REMOVE = new String[] { "Remove properties", };
//
//	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
//			Session session) {
//		String name = ((String) opts.get("-n")).trim();
//		((AlarmConfig) _config).removeProperty(name);
//		_config.updateConfig(false);
//		return 0;
//	}
//
	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all properties", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(((AlarmConfig) _config).getProperty(null));
		return 0;
	}
	
}
