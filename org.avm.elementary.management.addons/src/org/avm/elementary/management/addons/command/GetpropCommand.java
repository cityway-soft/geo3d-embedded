package org.avm.elementary.management.addons.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.BundleContext;

/**
 * Commande de r&eacute;cup&eacute;ration des variables d'environnement java
 * 
 * @author didier.lallemand@mercur.fr
 */
class GetpropCommand extends AbstractCommand {

	protected Enumeration getFields(String keys) {
		Vector v = null;
		if (keys == null)
			return null;
		StringTokenizer t = new StringTokenizer(keys, ",");
		if (t.hasMoreElements()) {
			v = new Vector();
		}
		while (t.hasMoreElements()) {
			String token = t.nextToken();
			v.addElement(token);
		}
		if (v != null) {
			return v.elements();
		} else {
			return null;
		}
	}

	private Enumeration getSystemPropertiesByFilter(String filter) {
		Enumeration e = System.getProperties().keys();
		Vector v = new Vector();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (key.indexOf(filter) != -1) {
				v.add(key);
			}
		}
		return v.elements();
	}

	/**
	 * @param context
	 * @param parameters
	 * <br>
	 *            Proprietes specifiques:
	 *            <ul>
	 *            <li><code>"keys"</code> : liste des cl&eacute;s dont il faut
	 *            retourner la valeur. <br>
	 *            Chaque cl&eacute; est s&eacute;par&eacute;e par une virgule. <br>
	 *            Ex: <code>keys=java.home,mavariableenv</code>
	 *            <li><code>"report"</code> [true/false] : envoi du resultat de
	 *            la commande sur le site ftp
	 *            </ul>
	 * @param out
	 * @param management
	 * @throws CommandException
	 * @throws IOException
	 */
	public void execute(BundleContext context, Properties parameters,
			PrintWriter out, ManagementService management)
			throws CommandException {

		try {
			boolean isReportRequired = Boolean.valueOf(
					parameters.getProperty("report", "true")).booleanValue();

			Enumeration enumeration = getFields(parameters.getProperty("keys"));
			StringBuffer buffer = new StringBuffer();
			if (enumeration == null) {
				Properties p = System.getProperties();
				enumeration = p.keys();
			}

			while (enumeration.hasMoreElements()) {
				String key = (String) enumeration.nextElement();
				Enumeration e = getSystemPropertiesByFilter(key);
				while (e.hasMoreElements()) {
					String k = (String) e.nextElement();
					String value = System.getProperty(k, "");
					buffer.append(k);
					buffer.append("=");
					buffer.append(value);
					buffer.append(System.getProperty("line.separator"));
				}
			}
			out.println(buffer);

		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
	}

	public static class GetpropCommandFactory extends CommandFactory {
		protected Command create() {
			return new GetpropCommand();
		}
	}

	static {
		CommandFactory.factories.put(GetpropCommand.class.getName(),
				new GetpropCommandFactory());
	}

}
