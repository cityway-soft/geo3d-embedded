package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

class RefreshCommand extends AbstractCommand {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		Vector vect = new Vector();
		String list = p.getProperty("bundle");
		Bundle[] bundles = null;
		if (list != null) {
			if (list.toLowerCase().trim().equals("all")) {
				bundles = context.getBundles();
			} else {
				StringTokenizer t = new StringTokenizer(list, " ");
				while (t.hasMoreElements()) {
					String bundleName = (String) t.nextElement();
					if (bundleName.length() > 0) {
						Bundle bundle = null;
						bundle = getBundle(context, bundleName);
						if (bundle != null) {
							vect.add(bundle);
						} else {
							out.println("Bundle '" + bundleName
									+ "' does not exist !");
						}
					}
				}
				Object[] objs = vect.toArray();
				bundles = new Bundle[objs.length];
				for (int i = 0; i < bundles.length; i++) {
					bundles[i] = (Bundle) objs[i];
				}
			}
		}
		management.getPackageAdminService().refreshPackages(bundles);
		management.getPackageAdminService().resolveBundles(bundles);
	}

	public static class RefreshCommandFactory extends CommandFactory {
		protected Command create() {
			return new RefreshCommand();
		}
	}

	static {
		CommandFactory.factories.put(RefreshCommand.class.getName(),
				new RefreshCommandFactory());
	}

}
