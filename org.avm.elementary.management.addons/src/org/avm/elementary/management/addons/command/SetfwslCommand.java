package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.BundleContext;

class SetfwslCommand extends AbstractCommand {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		String bundleStartlevel = p.getProperty("startlevel");

		if (bundleStartlevel == null) {
			out.println("Framework start level :"
					+ management.getStartLevelService().getStartLevel());

		} else {
			try {
				int sl = Integer.parseInt(bundleStartlevel);
				management.getStartLevelService().setStartLevel(sl);

			} catch (NumberFormatException e) {
				out.println("ERROR : " + e.getMessage());
			}
		}
	}

	public static class SetbslCommandFactory extends CommandFactory {
		protected Command create() {
			return new SetfwslCommand();
		}
	}

	static {
		CommandFactory.factories.put(SetfwslCommand.class.getName(),
				new SetbslCommandFactory());
	}

}
