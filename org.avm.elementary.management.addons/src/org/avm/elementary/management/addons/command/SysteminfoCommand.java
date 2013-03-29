package org.avm.elementary.management.addons.command;

import java.io.PrintWriter;
import java.util.Properties;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.BundleContext;

class SysteminfoCommand extends AbstractCommand {

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {
		long freememory = Runtime.getRuntime().freeMemory();
		long totalmemory = Runtime.getRuntime().totalMemory();
		int totalActiveThreads = Thread.activeCount();
		StringBuffer buf = new StringBuffer();
		buf.append("Free  memory........... :");
		buf.append(freememory);
		buf.append(LINE_SEPARATOR);
		buf.append("Total memory........... :");
		buf.append(totalmemory);
		buf.append(LINE_SEPARATOR);
		buf.append("Total active threads... :");
		buf.append(totalActiveThreads);
		buf.append(LINE_SEPARATOR);

		Thread[] threads = new Thread[totalActiveThreads];
		Thread.enumerate(threads);
		for (int i = 0; i < threads.length; i++) {
			buf.append("--- #" + i + " :" + threads[i]);
			buf.append(LINE_SEPARATOR);
		}

		out.println(buf.toString());
	}


	
	
	public static class SystemInfoCommandFactory extends CommandFactory {
		protected Command create() {
			return new SysteminfoCommand();
		}
	}

	static {
		CommandFactory.factories.put(SysteminfoCommand.class.getName(),
				new SystemInfoCommandFactory());
	}

}
