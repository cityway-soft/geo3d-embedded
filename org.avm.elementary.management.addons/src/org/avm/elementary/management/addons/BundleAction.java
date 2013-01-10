package org.avm.elementary.management.addons;

import java.io.PrintWriter;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public interface BundleAction {

	void execute(Bundle bundle, BundleContext context, PrintWriter out);

}
