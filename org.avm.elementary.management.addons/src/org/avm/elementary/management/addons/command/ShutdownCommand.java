package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.BundleContext;

class ShutdownCommand extends AbstractCommand {

	private ManagementService _management;

	public static final int REBOOT_SYSTEM = 3;

	public static final int REBOOT_FRAMEWORK = 2;

	public static final int REBOOT_FRAMEWORK_IN_DEBUG_MODE = 1;

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {

		_management = management;
		int exitCode = 0;

		int timeout = Integer.parseInt(p.getProperty("timeout", "15"));
		String reboot = p.getProperty("reboot");
		if (reboot != null) {
			if (reboot.equals("fwk")) {
				exitCode = 1;
			} else if (reboot.equals("debug")) {
				exitCode = 2;
			} else if (reboot.equals("sys")) {
				exitCode = 3;
			}
		} else {
			exitCode = 0;
		}
		_management.shutdown(out, timeout, exitCode);
	}

	public static class ShutdownCommandFactory extends CommandFactory {
		protected Command create() {
			return new ShutdownCommand();
		}
	}

	static {
		CommandFactory.factories.put(ShutdownCommand.class.getName(),
				new ShutdownCommandFactory());
	}

}
