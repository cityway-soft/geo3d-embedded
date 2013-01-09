package org.avm.device.generic.can.manager.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Properties;

import org.avm.device.generic.can.manager.CanManagerConfig;
import org.avm.device.generic.can.manager.CanManagerImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup implements Constants {

	public static final String COMMAND_GROUP = "can.manager";

	private CanManagerImpl _peer;

	CommandGroupImpl(ComponentContext context, CanManagerImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the can manager.");
		_peer = peer;
	}
	
	// Add
	public final static String USAGE_ADD = "-n #pid# -p #protocol#";

	public final static String[] HELP_ADD = new String[] { "Add can device", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String name = ((String) opts.get("-n")).trim();
		String protocol = ((String) opts.get("-p")).trim();
		
		Properties p = new Properties();
		p.put(SERVICE_PID, name);
		p.put(CanManagerConfig.PROTOCOL_NAME_TAG, protocol);

		((CanManagerConfig) _config).add(p);

		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-n #name#";

	public final static String[] HELP_REMOVE = new String[] { "Remove can device", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("-n")).trim();
		((CanManagerConfig) _config).remove(name);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all can device", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(((CanManagerConfig) _config));
		return 0;
	}

}
