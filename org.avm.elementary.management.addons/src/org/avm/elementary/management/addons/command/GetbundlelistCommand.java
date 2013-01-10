package org.avm.elementary.management.addons.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementService;
import org.avm.elementary.management.addons.Utils;
import org.avm.elementary.management.core.BundleList;
import org.avm.elementary.management.core.BundleProperties;
import org.avm.elementary.management.core.Management;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

class GetbundlelistCommand extends AbstractCommand {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) throws CommandException, IOException {

		BundleList bundleList = filteredBundleList(context, management, out);
		if (bundleList != null) {
			out.println("#######################################");
			out.println(bundleList);
			out.println("#######################################");
		} else {
			out.println("Cannot get bundles list !");
		}

	}

	/**
	 * converti un numero de version au format 1.0.0.1.2 dans un format
	 * numérique 1.0012 (pour pouvoir effectuer des comparaisons)
	 * 
	 * @param version
	 * @return
	 */
	private double getVersion(String version) {
		if (version == null)
			return Double.NaN;
		StringBuffer buf = new StringBuffer();
		boolean dotfound = false;
		for (int i = 0; i < version.length(); i++) {
			char c = version.charAt(i);
			if (Character.isDigit(c)) {
				buf.append(c);
			} else if (dotfound == false && c == '.') {
				dotfound = true;
				buf.append(c);
			}
		}
		double ver;
		;
		try {
			ver = Double.parseDouble(buf.toString());
		} catch (NumberFormatException e) {
			ver = Double.MAX_VALUE;
		}
		return ver;
	}
	
	private String getBundleListURL(String filename, boolean defaut, ManagementService management) {
		String vehiculeid = System.getProperty(Management.VEHICULE_PROPERTY);
		if (defaut) {
			vehiculeid = "default";
		}
		String exploitant = System
				.getProperty(Management.EXPLOITATION_PROPERTY);
		String plateform = System.getProperty(Management.PLATEFORM_PROPERTY);
		String strurl = management.getDownloadURL() + "/" + filename;
		strurl = Utils.formatURL(strurl, vehiculeid, exploitant, plateform);
		return strurl;
	}

	private BundleList getBundleList(String filename,
			ManagementService management) {
		String strurl = getBundleListURL(filename, false, management);
		BundleList bundleList = null;

		try {
			bundleList = loadFromURL(strurl);
			System.out.println(strurl + "|vehicule===>" + bundleList);
		} catch (IOException e) {
		}
		
		if (bundleList == null) {
			strurl = getBundleListURL(filename, true, management);
			try {
				bundleList = loadFromURL(strurl);
				System.out.println(strurl + "|default===>" + bundleList);
			} catch (IOException e) {
			}
		}
		return bundleList;
	}

	private BundleList downloadBundleList(ManagementService management)
			throws IOException {
		BundleList bundleList = null;
		bundleList = getBundleList(BundleList.FILENAME
				+ BundleList.COMPRESSED_EXT, management);
		if (bundleList == null) {
			bundleList = getBundleList(BundleList.FILENAME, management);
		}
		return bundleList;
	}

	/**
	 * retourne une hashtable contenant la liste des bundles sur le repository
	 * 
	 * @return
	 * @throws IOException
	 * @throws IOException
	 * @throws IOException
	 */
	public BundleList filteredBundleList(BundleContext context,
			ManagementService management, PrintWriter out) throws IOException {
		BundleList bundleList;

		bundleList = downloadBundleList(management);
		if (bundleList == null){
			return null;
		}

		try {
			out.println("[update] BundleList count : " + bundleList.size());

			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				String bundleName = bundles[i].getSymbolicName();

				BundleProperties bundleProperties = bundleList
						.getBundleProperties(bundleName);
				if (bundleProperties == null) {
					continue;
				}

				String ver = bundleProperties.getVersion();
				double bundleVersion = getVersion(ver);
				ver = (String) bundles[i].getHeaders().get("Bundle-Version");
				double currentVersion = getVersion(ver);

				if (bundleVersion <= currentVersion
						&& bundleProperties.getStartlevel() > 0) {
					bundleList.remove(bundleName);
				}
			}

			Enumeration e = bundleList.elements();
			while (e.hasMoreElements()) {
				BundleProperties bl = (BundleProperties) e.nextElement();
				String bundlename = bl.getName();
				Bundle bundle = getBundle(context, bundlename);
				if (bundle == null && bl.getStartlevel() <= 0) {
					bundleList.remove(bundlename);
				}
			}
		} catch (Throwable t) {
			out.println("ERREUR !!!!!!!!!");
			t.printStackTrace();
		}
		return bundleList;

	}

	private BundleList loadFromURL(String strurl) throws IOException,
			ConnectException {
		URL url = new URL(strurl);
		BundleList bundleList = BundleList.load(url);
		return bundleList;
	}

	public static class GetbundlelistCommandFactory extends CommandFactory {
		protected Command create() {
			return new GetbundlelistCommand();
		}
	}

	static {
		CommandFactory.factories.put(GetbundlelistCommand.class.getName(),
				new GetbundlelistCommandFactory());
	}

}
