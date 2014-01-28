package org.avm.elementary.configurator.bundle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Properties;

import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "configurator";

	CommandGroupImpl(ComponentContext context, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for all services configurator.");
	}

	// Configure
	public final static String USAGE_CONFIGURE = "<pid> [<keyvalue>] ...";

	public final static String[] HELP_CONFIGURE = new String[] { "<pid> [<key=value>] ..." };

	public int cmdConfigure(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String pid = (String) opts.get("pid");
		String[] keysValues = (String[]) opts.get("keyvalue");

		ConfigurationAdmin cm = (ConfigurationAdmin) _context
				.locateService("cm");
		if (keysValues != null) {
			try {
				Configuration configuration = cm.getConfiguration(pid);
				Dictionary oldDictionary = configuration.getProperties();
				if (oldDictionary == null) {
					oldDictionary = new Properties();
				}
				String b;
				int firstIndex;
				for (int i = 0; i < keysValues.length; i++) {
					b = keysValues[i];
					firstIndex = b.indexOf('=');
					try {
						oldDictionary.put(b.substring(0, firstIndex).trim(),
								b.substring(firstIndex + 1).trim());
					} catch (Exception e) {
						// LOG!
					}
				}
				configuration.update(oldDictionary);
			} catch (IOException e) {
				// LOG!
			}

			//
			// {
			// final BundleContext context = _context.getBundleContext();
			// final Bundle bundle = context.getBundle();
			// BundleListener listener = new BundleListener() {
			//
			// public void bundleChanged(BundleEvent event) {
			// if (event.getType() == BundleEvent.STOPPED
			// && event.getBundle().equals(bundle)) {
			// try {
			// bundle.start();
			// } catch (BundleException e) {
			// // LOG!
			// }
			// context.removeBundleListener(this);
			// }
			// }
			// };
			//
			// context.addBundleListener(listener);
			// try {
			// bundle.stop();
			// } catch (BundleException e) {
			// // LOG!
			// }
			// }

		}

		return 0;
	}
}
