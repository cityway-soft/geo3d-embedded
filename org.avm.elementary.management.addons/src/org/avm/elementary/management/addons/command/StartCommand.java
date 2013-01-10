package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

class StartCommand extends AbstractCommand {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		String list = p.getProperty("bundle");
		if (list != null) {
			StringTokenizer t = new StringTokenizer(list, " ");
			while (t.hasMoreElements()) {
				String bundleName = (String) t.nextElement();
				if (bundleName.equals("/all")) {
					out.println("Starting all bundles...");
					out.flush();
					Bundle[] bundles = context.getBundles();
					int count = 0;
					for (int i = 1; i < bundles.length; i++) {
						try {
							out.print("starting : "
									+ bundles[i].getSymbolicName());
							if (bundles[i].getState() == Bundle.RESOLVED) {
								bundles[i].start();
							}
							count++;
							out.println("[OK]");
						} catch (Exception e) {
							out.println("[ERROR] : " + e.getMessage());
						}
					}
					out.println(count + "/" + bundles.length
							+ " bundles started.");
				} else if (bundleName.length() > 0) {
					Bundle[] bundles = getBundles(context, bundleName);
					if (bundles != null && bundles.length != 0) {
						if (bundles.length == 1) {
							Bundle bundle = bundles[0];
							try {
								bundle.start();
							} catch (BundleException e) {
								out.println("ERROR : " + e.getMessage());
							}
						} else {
							out.println("Multiple matching bundles for '"
									+ bundleName + "':");
							for (int i = 0; i < bundles.length; i++) {
								out.println("- ID " + bundles[i].getBundleId()
										+ ") " + bundles[i].getSymbolicName());
							}
						}
					} else {
						out.println("ERROR : bundle named '" + bundleName
								+ "' does not exist.");
					}
				}
			}
		}

	}

	public String getName() {
		return "start";
	}

	public static class StartCommandFactory extends CommandFactory {
		protected Command create() {
			return new StartCommand();
		}
	}

	static {
		CommandFactory.factories.put(StartCommand.class.getName(),
				new StartCommandFactory());
	}

}
