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
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

class UninstallCommand extends AbstractCommand {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		String list = p.getProperty("bundle");
		if (list != null) {
			StringTokenizer t = new StringTokenizer(list, " ");
			while (t.hasMoreElements()) {
				String bundleName = (String) t.nextElement();
				uninstallBundle(context, bundleName, out, management);
			}
		}
	}

	private void uninstallBundle(BundleContext context, String bundleName,
			PrintWriter out, ManagementService management) {
		if (bundleName != null) {
			ServiceReference sr = context
					.getServiceReference(PackageAdmin.class.getName());
			// PackageAdmin packageAdmin = (PackageAdmin)
			// context.getService(sr);
			Bundle[] bundles = getBundles(context, bundleName);
			if (bundles != null && bundles.length != 0) {
				if (bundles.length == 1) {
					Bundle bundle = bundles[0];
					try {
						bundle.uninstall();
					} catch (BundleException e) {
						out.println("ERROR : " + e.getMessage());
					}
				} else {
					out.println("Multiple matching bundles for '" + bundleName
							+ "':");
					for (int i = 0; i < bundles.length; i++) {
						out.println("- ID " + bundles[i].getBundleId() + ") "
								+ bundles[i].getSymbolicName());
					}

				}
			} else {
				out.println("ERROR : bundle named '" + bundleName
						+ "' does not exist.");
			}
		}
	}

	public static class UninstallCommandFactory extends CommandFactory {
		protected Command create() {
			return new UninstallCommand();
		}
	}

	static {
		CommandFactory.factories.put(UninstallCommand.class.getName(),
				new UninstallCommandFactory());
	}

}
