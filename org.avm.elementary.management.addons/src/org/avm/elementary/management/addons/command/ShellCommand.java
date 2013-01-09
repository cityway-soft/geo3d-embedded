package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.BundleAction;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

class ShellCommand extends AbstractCommand implements BundleAction {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		String sparameters = p.getProperty("cmd");
		Shell.getInstance().setOutput(out);
		Shell.getInstance().execute(sparameters);
	}

	public String getName() {
		return "shell";
	}

	public static class ShellCommandFactory extends CommandFactory {
		protected Command create() {
			return new ShellCommand();
		}
	}

	static {
		CommandFactory.factories.put(ShellCommand.class.getName(),
				new ShellCommandFactory());
	}

	public void execute(Bundle bundle, BundleContext context, PrintWriter out) {

	}

}
