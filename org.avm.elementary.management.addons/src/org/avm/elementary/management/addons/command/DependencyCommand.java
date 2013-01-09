package org.avm.elementary.management.addons.command;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.BundleAction;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.Constants;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

class DependencyCommand extends AbstractCommand implements BundleAction {

	public void execute(BundleContext context, Properties p, PrintWriter out,
			ManagementService management) {
		String filter = p.getProperty("bundle");
		execute(this, filter, context, out, false);
	}

	public String getName() {
		return "dependency";
	}

	public static class DependencyCommandFactory extends CommandFactory {
		protected Command create() {
			return new DependencyCommand();
		}
	}

	static {
		CommandFactory.factories.put(DependencyCommand.class.getName(),
				new DependencyCommandFactory());
	}

	public void execute(Bundle bundle, BundleContext context, PrintWriter out) {
		String components = (String) bundle.getHeaders().get(
				"Service-Component");

		if (components != null && !components.trim().equals("")) {
			StringTokenizer t = new StringTokenizer(components, ",");
			while (t.hasMoreElements()) {
				String component = (String) t.nextElement();
				check(bundle, context, component, out);
			}
		}
	}

	private void check(Bundle bundle, BundleContext context, String component,
			PrintWriter out) {
		URL url = bundle.getEntry(component);
		if (url == null)
			return;
		try {
			DataInputStream zip = new DataInputStream(url.openStream());
			String line;
			StringBuffer buf = new StringBuffer();
			while ((line = zip.readLine()) != null) {
				buf.append(line);
			}
			zip.close();
			StringTokenizer t = new StringTokenizer(buf.toString(), ">");
			int i = 0;
			while (t.hasMoreElements()) {
				line = t.nextToken().trim() + ">";
				if (line.indexOf("reference") == -1)
					continue;
				if (checkLine(context, line, out) == false) {
					if (i == 0) {
						out.println(" Unresolved reference for "
								+ bundle.getSymbolicName() + "("
								+ bundle.getBundleId() + ") :");
						i++;
					}
					String reference = parseReference(line);
					out.println("\t\t" + Constants.SETCOLOR_FAILURE + reference
							+ Constants.SETCOLOR_NORMAL);
				}
			}
		} catch (IOException e) {
			out.println("Error : " + e);
			e.printStackTrace();
		}
	}

	private boolean checkLine(BundleContext context, String line,
			PrintWriter out) {
		String clazz = null;
		if (line.indexOf("reference") != -1) {
			String temp = line.substring(line.indexOf("interface=") + 11);
			clazz = temp.substring(0, temp.indexOf("\""));
		}
		if (clazz != null && !checkService(context, clazz, out)) {
			return false;
		}

		return true;
	}

	private boolean checkService(BundleContext context, String clazz,
			PrintWriter out) {
		boolean result = false;
		try {
			ServiceReference sr = context.getServiceReference(clazz);
			result = (sr != null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String parseReference(String line) {
		String l = line.replace('\n', ' ');
		l = l.replace('\t', ' ');
		l = l.replace('>', ' ');
		l = l.replace('/', ' ');
		StringTokenizer t = new StringTokenizer(l, " ");
		StringBuffer buf = new StringBuffer();
		while (t.hasMoreElements()) {
			String tag = (String) t.nextElement();

			tag = tag.trim();
			if (tag.indexOf("name") != -1
					|| tag.trim().indexOf("interface") != -1) {
				buf.append(tag);
				buf.append(" ");
			}
		}
		return buf.toString();
	}

}
