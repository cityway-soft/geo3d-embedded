package org.avm.elementary.management.addons.command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementService;
import org.avm.elementary.management.core.utils.Terminal;
import org.osgi.framework.BundleContext;

/**
 * Commande de r&eacute;cup&eacute;ration des variables d'environnement java
 * 
 * @author didier.lallemand@mercur.fr
 */
class ConfigurationCommand extends AbstractCommand {

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
			PrintWriter pout, ManagementService management)
			throws CommandException {

		String action = parameters.getProperty("action");
		String propName = parameters.getProperty("property");
		String propertyfile = parameters.getProperty("filename",
				"avm.properties");

		PrintWriter out = pout;
		if (out == null) {
			out = new PrintWriter(System.out);
		}
		if (action != null) {
			if (action.equals("load")) {
				Properties avmproperties = loadProperties(propertyfile, out);
				setSystemProperties(avmproperties);
				if (out != null) {
					out.println("load avm.properties:");
					out.println(avmproperties);
				}
			} else if (action.equals("save")) {
				Properties avmproperties = loadProperties(propertyfile, out);
				if (propName != null) {
					avmproperties.put(propName, System.getProperty(propName,
							"empty"));
				}
				saveProperties(propertyfile, avmproperties, out);
			}

		}
	}

	private Properties loadProperties(String filename, PrintWriter out) {
		Properties p;
		String pathfile = null;

		p = new Properties();
		try {
			pathfile = getFilePath(filename);
			p.load(new FileInputStream(pathfile));
		} catch (FileNotFoundException e) {
			out.println("FileNotFoundException (" + e.getMessage() + "): "
					+ pathfile);
		} catch (IOException e) {
			out.println("IOException (" + e.getMessage() + "): " + pathfile);
		}
		
		return p;
	}
	
	private String getFilePath(String filename) {
		StringBuffer buf = new StringBuffer();
		buf.append(System.getProperty("org.avm.home"));
		buf.append(System.getProperty("file.separator"));
		buf.append("bin");
		buf.append(System.getProperty("file.separator"));
		buf.append(filename);
		return buf.toString();

	}

	private void setSystemProperties(Properties props) {
		Enumeration e = props.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			System.setProperty(key, props.getProperty(key));
		}
	}


	
	private void saveProperties(String filename, Properties p, PrintWriter out) {
		Properties props = new Properties();
		Enumeration e = p.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (System.getProperty(key) != null
					&& !System.getProperty(key).trim().equals("")) {
				props.setProperty(key, System.getProperty(key));
			}
		}

		String filepath = getFilePath(filename);
		try {
			props.save(new FileOutputStream(filepath), new Date().toString());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		out.println(props);
		out.println("Properties saved in " + filename);

	}

	public static class ConfigurationCommandFactory extends CommandFactory {
		protected Command create() {
			return new ConfigurationCommand();
		}
	}

	static {
		CommandFactory.factories.put(ConfigurationCommand.class.getName(),
				new ConfigurationCommandFactory());
	}

}
