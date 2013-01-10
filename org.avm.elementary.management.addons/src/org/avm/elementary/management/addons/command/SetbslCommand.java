package org.avm.elementary.management.addons.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Commande permettant de positionner le "startlevel" d'un bundle
 * 
 * @author didier.lallemand@mercur.fr
 */
class SetbslCommand extends AbstractCommand {
	/**
	 * @param context
	 * @param parameters
	 * <br>
	 *            Proprietes specifiques:
	 *            <ul>
	 *            <li><code>"bundle"</code> : bundle (symbolic name ou bundle
	 *            id) dont il faut positionner le "startlevel". <br>
	 *            Ex: <code>bundle=org.avm.elementary.messenger</code>
	 *            <li><code>"startlevel"</code> : valeur du "startlevel". <br>
	 *            Ex: <code>startlevel=6</code>
	 *            </ul>
	 * @param out
	 * @param management
	 * @throws CommandException
	 * @throws IOException
	 */
	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		String bundleName = p.getProperty("bundle");
		String bundleStartlevel = p.getProperty("startlevel");

		if (bundleName != null) {
			Bundle[] bundles = getBundles(context, bundleName);
			if (bundles != null) {
				if (bundles.length == 1) {
					Bundle bundle = bundles[0];
					if (bundleStartlevel == null) {
						out.println(bundle.getSymbolicName()
								+ " starts in level "
								+ management.getStartLevelService()
										.getBundleStartLevel(bundle));
						out.flush();
					} else {
						try {
							int sl = Integer.parseInt(bundleStartlevel);
							management.getStartLevelService()
									.setBundleStartLevel(bundle, sl);
						} catch (NumberFormatException e) {
							out.println("ERROR: NumberFormatException "
									+ e.getMessage());
						}
					}

				}
			} else {
				out.println("ERROR : bundle named '" + bundleName
						+ "' does not exist.");
			}

		}
	}

	public static class SetbslCommandFactory extends CommandFactory {
		protected Command create() {
			return new SetbslCommand();
		}
	}

	static {
		CommandFactory.factories.put(SetbslCommand.class.getName(),
				new SetbslCommandFactory());
	}

}
