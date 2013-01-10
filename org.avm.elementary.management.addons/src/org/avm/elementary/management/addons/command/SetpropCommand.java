package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.BundleContext;

class SetpropCommand extends AbstractCommand {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {
		Enumeration enumeration = p.keys();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			String value = p.getProperty(key, "");
			System.setProperty(key, value);
		}
		out.println(p.toString());
	}

	public static class SetpropCommandFactory extends CommandFactory {
		protected Command create() {
			return new SetpropCommand();
		}
	}

	static {
		CommandFactory.factories.put(SetpropCommand.class.getName(),
				new SetpropCommandFactory());
	}

}
