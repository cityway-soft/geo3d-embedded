package org.avm.elementary.management.addons.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.avm.elementary.management.addons.Utils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

class UpdateCommand extends AbstractCommand {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		String list = p.getProperty("bundle");
		Hashtable hashBundleState = new Hashtable();
		Hashtable hashBundleURL = new Hashtable();

		if (list != null) {
			// - construction de la liste des bundles � installer ou 'updater'
			// - memorisation de leur �tat
			// - 'stop' sur chacun de ces bundles
			// - 'install' ou 'update' de chacun des bundles
			// - redemarrage des bundles s'ils �taient d�marr�s
			StringTokenizer t = new StringTokenizer(list, " ");

			while (t.hasMoreElements()) {
				String bundleName = (String) t.nextElement();

				String bundleURL = null;
				if (bundleName.startsWith("http://") || bundleName.startsWith("ftp://")|| bundleName.startsWith("file://")){
					int idx=bundleName.indexOf(".jar");
					if (idx != -1){
						String temp=bundleName.substring(0, idx);
						//System.out.println("[Management - UpdateCommand] temp="+temp);
						idx=temp.lastIndexOf('/');
						//System.out.println("[Management - UpdateCommand] idx="+idx);
						bundleURL=temp.substring(0, idx);
						//System.out.println("[Management - UpdateCommand] strurl="+strurl);
						bundleName=temp.substring(idx+1);
						//System.out.println("[Management - UpdateCommand] bundleRef="+bundleRef);
					}
				}
				else{
					bundleURL = management.getDownloadURL().toString();
				}
				hashBundleURL.put(bundleName, bundleURL);

				Bundle[] bundles = getBundles(context, bundleName);
				Bundle bundle = null;
				if (bundles != null && bundles.length != 0) {
					if (bundles.length == 1) {
						bundle = bundles[0];
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

				if (this.getClass().getPackage().getName().indexOf(bundleName) != -1) {
					// -- attention : ne pas stopper le bundle management (sinon
					// redemarrage manuel!)
					out.print(" update cannot stop management!!!!! ");
					out.flush();
					continue;
				}

				if (bundle != null && bundle.getState() == Bundle.ACTIVE) {
					hashBundleState.put(bundleName, new Boolean(true));
					try {
						out.print("stop bundle " + bundleName);
						bundle.stop();
						out.println("[done]");
					} catch (BundleException e1) {
						out.println("[Error]" + e1.getMessage());
					}
				} else {
					hashBundleState.put(bundleName, new Boolean(false));
				}
			}

			Enumeration e = hashBundleState.keys();
			while (e.hasMoreElements()) {
				String bundleName = (String) e.nextElement();
				out.print("updating " + bundleName + "...");
				String bundleURL = (String)hashBundleURL.get(bundleName);
				deployBundle(context, bundleURL, bundleName, out, management);
				out.print(bundleName + " updated.");
			}

			Bundle[] bundles = new Bundle[hashBundleState.size()];
			int i = 0;
			e = hashBundleState.keys();
			while (e.hasMoreElements()) {
				String bundleName = (String) e.nextElement();
				bundles[i] = getBundle(context, bundleName);
				out.println("bundle to refresh: " + bundleName + "("
						+ bundles[i] + ") state=" + bundles[i].getState());
				i++;
			}
			try {
				out.print("refreshing bundles...");
				management.getPackageAdminService().refreshPackages(bundles);
				out.println("[done]");
				// management.getPackageAdminService().resolveBundles(bundles);
			} catch (Throwable th) {
				out.println("[Error] on refresh bundles " + th.getMessage());
			}
			e = hashBundleState.keys();
			while (e.hasMoreElements()) {
				String bundleName = (String) e.nextElement();
				boolean wasActived = ((Boolean) hashBundleState.get(bundleName))
						.booleanValue();
				if (wasActived) {
					Bundle bundle = getBundle(context, bundleName);
					if (bundle != null) {
						try {
							out.print("starting bundle " + bundleName);
							bundle.start();
							out.println("[done]");
						} catch (BundleException e1) {
							out.println("[Error]" + e1.getMessage());
						}
					}
				}
			}

		}
	}

	public void deployBundle(BundleContext context, String strurl, String bundleRef,
			PrintWriter out, ManagementService management) {
		if (bundleRef != null) {

			
			
			Bundle[] bundles = getBundles(context, bundleRef);
			Bundle bundle = null;
			if (bundles != null && bundles.length != 0) {
				if (bundles.length == 1) {
					bundle = bundles[0];
				} else {
					out.println("Multiple matching bundles for '" + bundleRef
							+ "':");
					for (int i = 0; i < bundles.length; i++) {
						out.println("- ID " + bundles[i].getBundleId() + ") "
								+ bundles[i].getSymbolicName());
					}
				}
			} else {
				out.println("ERROR : bundle named '" + bundleRef
						+ "' does not exist.");
			}

			try {
				if (bundle != null) {
					// -- bundle deja installé : mise à jour
					String bundleName = (String) bundle.getHeaders().get(
							"Bundle-SymbolicName"); //$NON-NLS-1$
					String jarfilename = bundleName + ".jar"; //$NON-NLS-1$

					bundle = update(strurl, jarfilename, bundle, out);

				} else {
					// -- bundle pas installé : installation
					bundle = install(context, strurl, bundleRef + ".jar", out);
				}
				if (bundle != null) {
					out.println("bundle id : " + bundle.getBundleId()); //$NON-NLS-1$
				} else {
					out.println("ERROR : unable to install " + bundleRef); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} catch (Exception e) {
				out
						.println("[update] on deployBundle " + bundleRef + " ERROR : " //$NON-NLS-1$ //$NON-NLS-2$
								+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private Bundle install(BundleContext context, String strurl,
			String filename, PrintWriter out) throws BundleException,
			IOException {
		//		 
		Bundle bundle = null;

		URL url;
		try {
			String surl = Utils.replace(strurl + "/" + filename, "$u", System
					.getProperty("org.avm.vehicule.id", "default"));
			if (out != null) {
				out.println("try to install bundle from " + surl); //$NON-NLS-1$
			}
			url = new URL(surl);
			URLConnection connection = url.openConnection();

			bundle = context.installBundle(surl, connection.getInputStream());
			if (out != null) {
				out
						.println("install " + bundle.getSymbolicName() + "  (" + bundle.getBundleId() + ")"); //$NON-NLS-1$
			}
		} catch (IOException e) {
			String surl = Utils.replace(strurl + "/" + filename, "$u",
					"default");
			if (out != null) {
				out.println("try to install bundle from default url " + surl); //$NON-NLS-1$
			}
			url = new URL(surl);
			URLConnection connection = url.openConnection();

			bundle = context.installBundle(surl, connection.getInputStream());
		}
		return bundle;
	}

	private Bundle update(String strurl, String filename, Bundle bundle,
			PrintWriter out) throws BundleException, IOException {
		try {
			String surl = Utils.replace(strurl + "/" + filename, "$u", System
					.getProperty("org.avm.vehicule.id", "default"));
			updateBundleFromUrl(bundle, surl, out);
		} catch (IOException e) {
			String surl = Utils.replace(strurl + "/" + filename, "$u",
					"default");
			if (out != null) {
				out.println("try to update bundle from default url " + surl); //$NON-NLS-1$
			}
			updateBundleFromUrl(bundle, surl, out);
		}
		return bundle;
	}

	private Bundle updateBundleFromUrl(Bundle bundle, String surl,
			PrintWriter out) throws IOException, BundleException {
		if (out != null) {
			out.println("try to update bundle from " + surl); //$NON-NLS-1$
		}

		URL url = new URL(surl);
		URLConnection connection = url.openConnection();
		bundle.update(connection.getInputStream());

		if (out != null) {
			out
					.println("update " + bundle.getSymbolicName() + "  (" + bundle.getBundleId() + ")"); //$NON-NLS-1$
		}

		return bundle;
	}

	public static class UpdateCommandFactory extends CommandFactory {
		protected Command create() {
			return new UpdateCommand();
		}
	}

	static {
		CommandFactory.factories.put(UpdateCommand.class.getName(),
				new UpdateCommandFactory());
	}

}
