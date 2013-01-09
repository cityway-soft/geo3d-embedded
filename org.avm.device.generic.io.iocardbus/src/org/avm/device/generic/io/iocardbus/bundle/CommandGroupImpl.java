package org.avm.device.generic.io.iocardbus.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.generic.io.iocardbus.IOCardBusConfig;
import org.avm.device.generic.io.iocardbus.IOCardBusImpl;
import org.avm.device.io.IOCardInfo;
import org.avm.device.io.iocardbus.IOCardBus;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "iocardbus";

	private IOCardBusImpl _peer;

	public CommandGroupImpl(ComponentContext context, IOCardBus peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the iocardbus.");
		_peer = (IOCardBusImpl) peer;
		_config = config;
	}

	// Add
	public final static String USAGE_ADD = "-n #name# -c #category# -m #model# -s #serial# [-o #manufacturer#]";

	public final static String[] HELP_ADD = new String[] { "Add IO Card", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String name = ((String) opts.get("-n")).trim();
		String category = ((String) opts.get("-c")).trim();
		String model = ((String) opts.get("-m")).trim();
		String serial = ((String) opts.get("-s")).trim();
		String manufacturer = ((String) opts.get("-o")).trim();

		IOCardInfo card = new IOCardInfo(category, manufacturer, model, serial);
		_peer.registerDevice(card);

		((IOCardBusConfig) _config).add(name, category, manufacturer, model,
				serial);
		_config.updateConfig(false);
		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-n #name#";

	public final static String[] HELP_REMOVE = new String[] { "Remove IO Card", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String name = ((String) opts.get("-n")).trim();
		IOCardInfo card = ((IOCardBusConfig) _config).getIOCCardInfo(name);
		if (card != null) {
			_peer.unregisterDevice(card);
			((IOCardBusConfig) _config).remove(name);
			_config.updateConfig(false);
		}

		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all IO Card", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(_config.toString());
		return 0;
	}
}
