package org.avm.elementary.management.addons.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Commande permettant de red&eacute;marrer les bundles en paramètres
 * 
 * @author didier.lallemand@mercur.fr
 */
class RestartCommand extends AbstractCommand {
	/**
	 * @param context
	 * @param parameters
	 * <br>
	 *            Proprietes specifiques:
	 *            <ul>
	 *            <li><code>"bundle"</code> : liste des bundles (symbolic name
	 *            ou bundle id) s&eacute;par&eacute;e par un espace &acute;
	 *            red&eacute;marrer. <br>
	 *            Ex: <code>bundle=12 org.avm.device.gsm</code>
	 *            </ul>
	 * @param out
	 * @param management
	 * @throws CommandException
	 * @throws IOException
	 */
	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {
		String list = p.getProperty("bundle");

		if (list != null) {
			StringTokenizer t = new StringTokenizer(list, " ");
			while (t.hasMoreElements()) {
				String bundleName = (String) t.nextElement();
				if (bundleName != null) {
					Bundle[] bundles = getBundles(context, bundleName);
					if (bundles != null && bundles.length != 0) {
						if (bundles.length == 1) {
							Bundle bundle = bundles[0];
							try {
								out.println(bundle + ": stopping...");
								out.flush();
								bundle.stop();
								out.println(bundle + ": stopped.");
								out.flush();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {

								}
								out.println(bundle + ": starting...");
								out.flush();
								bundle.start();
								out.println(bundle + ": started.");
								out.flush();
							} catch (BundleException e) {
								out.println("ERROR : " + e.getMessage());
							}
						} else {
							out.println("A few bundles matchs :");
							for (int i = 0; i < bundles.length; i++) {
								out.println("-> [" + bundles[i].getBundleId()
										+ "] " + bundles[i].getSymbolicName());
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

	public static class RestartCommandFactory extends CommandFactory {
		protected Command create() {
			return new RestartCommand();
		}
	}

	static {
		CommandFactory.factories.put(RestartCommand.class.getName(),
				new RestartCommandFactory());
	}

}
