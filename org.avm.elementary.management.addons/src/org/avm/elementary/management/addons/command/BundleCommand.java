package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.BundleAction;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.RequiredBundle;

class BundleCommand extends AbstractCommand implements BundleAction {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		String filter = p.getProperty("bundle");

		execute(this, filter, context, out, true);
	}

	public String getName() {
		return "bundle";
	}

	public static class BundleCommandFactory extends CommandFactory {
		protected Command create() {
			return new BundleCommand();
		}
	}

	static {
		CommandFactory.factories.put(BundleCommand.class.getName(),
				new BundleCommandFactory());
	}

	public void execute(Bundle bundle, BundleContext context, PrintWriter out) {
		ServiceReference[] services = bundle.getRegisteredServices();
		if (services != null) {
			out.println("  Registered services");
			out.println("  -------------------");
			for (int j = 0; j < services.length; j++) {
				out.print("    "); //$NON-NLS-1$
				out.println(services[j]);
			}
		} else {
			out.println("  No service registered.");
		}

		services = bundle.getServicesInUse();
		if (services != null) {
			out.println("  Service in use");
			out.println("  --------------");
			for (int j = 0; j < services.length; j++) {
				out.print("    "); //$NON-NLS-1$
				out.println(services[j]);
			}
		} else {
			out.print("  "); //$NON-NLS-1$
			out.println("  No service in use");
		}

		org.osgi.framework.ServiceReference packageAdminRef = context
				.getServiceReference("org.osgi.service.packageadmin.PackageAdmin"); //$NON-NLS-1$
		if (packageAdminRef != null) {
			org.osgi.service.packageadmin.PackageAdmin packageAdmin = (org.osgi.service.packageadmin.PackageAdmin) context
					.getService(packageAdminRef);
			if (packageAdmin != null) {
				try {
					org.osgi.service.packageadmin.ExportedPackage exportedpkgs[] = packageAdmin
							.getExportedPackages((org.osgi.framework.Bundle) null);

					if (exportedpkgs == null) {
						out.print("  "); //$NON-NLS-1$
						out.println("No exported packages");
						out.print("  "); //$NON-NLS-1$
						out.println("No imported packages");
					} else {
						boolean title = true;

						for (int i = 0; i < exportedpkgs.length; i++) {
							org.osgi.service.packageadmin.ExportedPackage exportedpkg = exportedpkgs[i];

							if (exportedpkg.getExportingBundle() == bundle) {
								if (title) {
									out.print("  "); //$NON-NLS-1$
									out.println("Exported packages");
									title = false;
								}
								out.print("    "); //$NON-NLS-1$
								out.print(exportedpkg);
								if (exportedpkg.isRemovalPending()) {
									out
											.println("EXPORTED_REMOVAL_PENDING_MESSAGE");
								} else {
									out.println("Exported");
								}
							}
						}

						if (title) {
							out.print("  "); //$NON-NLS-1$
							out.println("No exported packages");
						}

						title = true;

						for (int i = 0; i < exportedpkgs.length; i++) {
							org.osgi.service.packageadmin.ExportedPackage exportedpkg = exportedpkgs[i];

							org.osgi.framework.Bundle[] importers = exportedpkg
									.getImportingBundles();
							for (int j = 0; j < importers.length; j++) {
								if (importers[j] == bundle) {
									if (title) {
										out.print("  "); //$NON-NLS-1$
										out.println("Imported packages");
										title = false;
									}
									out.print("    "); //$NON-NLS-1$
									out.print(exportedpkg);
									org.osgi.framework.Bundle exporter = exportedpkg
											.getExportingBundle();
									if (exporter != null) {
										out.print("<"); //$NON-NLS-1$
										out.print(exporter);
										out.println(">"); //$NON-NLS-1$
									} else {
										out.print("<"); //$NON-NLS-1$
										out.print("STALE_MESSAGE");
										out.println(">"); //$NON-NLS-1$
									}

									break;
								}
							}
						}

						if (title) {
							out.print("  "); //$NON-NLS-1$
							out.println("No imported packages");
						}

						out.print("  "); //$NON-NLS-1$
						if ((packageAdmin.getBundleType(bundle) & 0x00000001) > 0) {
							org.osgi.framework.Bundle[] hosts = packageAdmin
									.getHosts(bundle);
							if (hosts != null) {
								out.println("Host");
								for (int i = 0; i < hosts.length; i++) {
									out.print("    "); //$NON-NLS-1$
									out.println(hosts[i]);
								}
							} else {
								out.println("No host");
							}
						} else {
							org.osgi.framework.Bundle[] fragments = packageAdmin
									.getFragments(bundle);
							if (fragments != null) {
								out.println("Fragment");
								for (int i = 0; i < fragments.length; i++) {
									out.print("    "); //$NON-NLS-1$
									out.println(fragments[i]);
								}
							} else {
								out.println("No fragment");
							}
						}

						RequiredBundle[] requiredBundles = packageAdmin
								.getRequiredBundles(null);
						RequiredBundle requiredBundle = null;
						if (requiredBundles != null) {
							for (int i = 0; i < requiredBundles.length; i++) {
								if (requiredBundles[i].getBundle() == bundle) {
									requiredBundle = requiredBundles[i];
									break;
								}
							}
						}

						if (requiredBundle == null) {
							out.print("  "); //$NON-NLS-1$
							out.println("No named class spaces");
						} else {
							out.print("  "); //$NON-NLS-1$
							out.println("Named class space");
							out.print("    "); //$NON-NLS-1$
							out.print(requiredBundle);
							if (requiredBundle.isRemovalPending()) {
								out.println("Removal pending");
							} else {
								out.println("Provided");
							}
						}

						title = true;
						for (int i = 0; i < requiredBundles.length; i++) {
							if (requiredBundles[i] == requiredBundle)
								continue;

							org.osgi.framework.Bundle[] depBundles = requiredBundles[i]
									.getRequiringBundles();
							if (depBundles == null)
								continue;
							for (int j = 0; j < depBundles.length; j++) {
								if (depBundles[j] == bundle) {
									if (title) {
										out.print("  "); //$NON-NLS-1$
										out.println("Required bundles");
										title = false;
									}
									out.print("    "); //$NON-NLS-1$
									out.print(requiredBundles[i]);

									org.osgi.framework.Bundle provider = requiredBundles[i]
											.getBundle();
									out.print("<"); //$NON-NLS-1$
									out.print(provider);
									out.println(">"); //$NON-NLS-1$
								}
							}
						}
						if (title) {
							out.print("  "); //$NON-NLS-1$
							out.println("No required bundle");
						}

					}
				} finally {
					context.ungetService(packageAdminRef);
				}
			}
		} else {
			out.print("  "); //$NON-NLS-1$
			out.println("No exported packages - no PackageAdmin");
		}

	}

}
