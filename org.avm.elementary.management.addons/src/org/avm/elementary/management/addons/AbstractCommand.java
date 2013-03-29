package org.avm.elementary.management.addons;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author MERCUR
 * 
 */
public abstract class AbstractCommand implements Command {

	public AbstractCommand() {
		// _log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	/**
	 * Retourne le bundle correspondant au nom <code>bundlename
	 * 
	 * @param context
	 * @param bundlename
	 * @return
	 */
	protected Bundle[] getBundles(BundleContext context, String bundlename) {
		Bundle bundles[];

		try {
			int bundleId = Integer.parseInt(bundlename);
			bundles = new Bundle[1];
			bundles[0] = getBundleById(context, bundleId);
			return bundles;
		} catch (Exception e) {

		}

		bundles = getBundleByFilter(context, bundlename);
		/*
		 * try { Bundle[] bundles = context.getBundles(); for (int i = 0; i <
		 * bundles.length; i++) { bundle = bundles[i]; // _log.debug("Bundle
		 * list:"); String installbundle = (String) bundle.getHeaders().get(
		 * "Bundle-SymbolicName"); installbundle =
		 * formatBundleName(installbundle); // _log.debug(" " + installbundle);
		 * if (installbundle != null && installbundle.equals(bundlename)) {
		 * return bundle; } } } catch (Exception e) { }
		 */
		return bundles;
	}

	protected Bundle getBundle(BundleContext context, String bundlename) {
		try {
			long bid = Long.parseLong(bundlename);
			return context.getBundle(bid);
		} catch (Exception e) {

		}

		try {
			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				Bundle bundle = bundles[i]; // _log.debug("Bundlelist:"); " +
				String installbundle = (String) bundle.getHeaders().get(
						"Bundle-SymbolicName");
				installbundle = formatBundleName(installbundle); // _log.debug("
				// " +
				// installbundle);
				if (installbundle != null && installbundle.equals(bundlename)) {
					return bundle;
				}
			}
		} catch (Exception e) {
		}

		return null;
	}

	private Bundle[] getBundleByFilter(BundleContext context, String filter) {
		Bundle[] bundles = context.getBundles();
		Vector v = new Vector();

		// aucun filtre
		if (filter == null) {
			for (int i = 0; i < bundles.length; i++) {
				Bundle b = bundles[i];
				v.addElement(b);
			}
		} else {
			for (int i = 0; i < bundles.length; i++) {
				Bundle b = bundles[i];
				if (b.getSymbolicName().indexOf(filter) != -1) {
					v.addElement(b);
				}
				if (b.getSymbolicName().equals(filter)) { // si strictement egal
					// alors plus
					// necessaire de
					// chercher...
					break;
				}
			}
		}

		Bundle[] res = null;
		if (v.size() != 0) {

			res = new Bundle[v.size()];
			int i = 0;
			while (i < v.size()) {
				res[i] = (Bundle) v.elementAt(i);
				i++;
			}
		}

		return res;
	}

	public String formatBundleName(String bundlename) {
		String result = bundlename;
		if (bundlename != null) {
			int idx = bundlename.indexOf(";");
			if (idx != -1) {
				result = bundlename.substring(0, idx);
			}
		}
		return result;
	}

	/**
	 * retourne le bundle dont le <i>Bundle Id</i> vaut <code>bundleid
	 * 
	 * @param context
	 * @param bundleid
	 * @return
	 */
	protected Bundle getBundleById(BundleContext context, int bundleid) {
		try {
			Bundle bundle = context.getBundle(bundleid);
			return bundle;
		} catch (Exception e) {

		}
		return null;
	}


	public String getName() {
		String pack = this.getClass().getPackage().getName();
		String classname = this.getClass().getName();
		int idx = classname.indexOf("Command");
		String name = classname.substring(pack.length(), pack.length() + idx);
		return name;
	}

	public void execute(BundleAction action, String filter,
			BundleContext context, PrintWriter out, boolean firstOnly) {
		Bundle[] bundles = getBundles(context, filter);

		if (bundles != null && bundles.length != 0) {
			if (firstOnly && bundles.length > 1) {
				out.println("Multiple matching bundles for '" + filter + "':");
				for (int i = 0; i < bundles.length; i++) {
					out.println("- ID " + bundles[i].getBundleId() + ") "
							+ bundles[i].getSymbolicName());
				}
			} else {
				int max = firstOnly ? 1 : bundles.length;
				for (int i = 0; i < max; i++) {
					Bundle b = bundles[i];
					action.execute(b, context, out);
				}
			}
		} else {
			out.println("no bundle matchs '" + filter + "'");
		}
	}

}
