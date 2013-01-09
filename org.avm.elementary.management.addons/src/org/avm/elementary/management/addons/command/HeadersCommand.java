package org.avm.elementary.management.addons.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Commande de r&eacute;cup&eacute;ration des propri&eacute;t&eacute;s du bundle
 * pr&eacute;sentent dans le fichier Manifest.
 * 
 * @author didier.lallemand@mercur.fr
 */
class HeadersCommand extends AbstractCommand {
	/**
	 * @param context
	 * @param parameters
	 * <br>
	 *            Aucune propri&eacute;t&eacute; sp&eacute;cifique
	 * @param out
	 * @param management
	 * @throws CommandException
	 * @throws IOException
	 */
	public void execute(BundleContext context, Properties parameters,
			PrintWriter out, ManagementService management) {

		String bundleName = null;

		bundleName = parameters.getProperty("bundle");

		Bundle[] bundles = getBundles(context, bundleName);
		if (bundles != null) {
			if (bundles.length == 1) {
				Bundle bundle = bundles[0];

				Dictionary dic = bundle.getHeaders();
				Enumeration e = dic.keys();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					String value = (String) dic.get(key);
					out.println(key + " = " + value);
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

	public static class HeadersCommandFactory extends CommandFactory {
		protected Command create() {
			return new HeadersCommand();
		}
	}

	static {
		CommandFactory.factories.put(HeadersCommand.class.getName(),
				new HeadersCommandFactory());
	}

}
