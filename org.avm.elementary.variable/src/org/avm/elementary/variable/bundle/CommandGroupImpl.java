package org.avm.elementary.variable.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.variable.Variable;
import org.avm.elementary.variable.VariableConfig;
import org.avm.elementary.variable.VariableService;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.Measurement;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "variables";

	private VariableService _peer;

	CommandGroupImpl(ComponentContext context, VariableService peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the variable.");
		_peer = peer;
	}

	// Add
	public final static String USAGE_ADD = "-n #name# -t #type# -c #category# -s #serial#  -i #index# [<args>] ...";

	public final static String[] HELP_ADD = new String[] { "Add variable", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String name = ((String) opts.get("-n")).trim();
		String type = ((String) opts.get("-t")).trim();
		String category = ((String) opts.get("-c")).trim();
		String serial = ((String) opts.get("-s")).trim();
		String index = ((String) opts.get("-i")).trim();
		String[] args = (String[]) opts.get("args");

		Properties p = new Properties();
		p.put(Variable.NAME, name);
		p.put(Variable.TYPE, type);
		p.put(Variable.DEVICE_CATEGORY, category);
		p.put(Variable.DEVICE_SERIAL, serial);
		p.put(Variable.DEVICE_INDEX, index);
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				String text = args[i];
				int n = text.indexOf('=');
				if (n != -1) {
					String key = text.substring(0, n);
					String value = text.substring(n + 1);
					p.put(key, value);
				}
			}
		}
		((VariableConfig) _config).add(p);

		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-n #name#";

	public final static String[] HELP_REMOVE = new String[] { "Remove variable", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("-n")).trim();
		((VariableConfig) _config).remove(name);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all variables", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		StringBuffer buf = new StringBuffer();
		Dictionary dic = ((VariableConfig) _config).get();
		Enumeration e = dic.keys();
		while(e.hasMoreElements()){
			String key = (String)e.nextElement();
			buf.append(key);
			buf.append(" = ");

			Properties props = (Properties)dic.get(key);
			Enumeration e2 = props.keys();
			buf.append("{");
			while(e2.hasMoreElements()){
				String key2 = (String)e2.nextElement();
				buf.append(System.getProperty("line.separator"));
				buf.append("\t");
				buf.append(key2);
				buf.append(" = ");
				buf.append(props.get(key2));
			}
			buf.append(System.getProperty("line.separator"));
			buf.append("}");
			buf.append(System.getProperty("line.separator"));
		}
		out.println(buf);
		return 0;
	}

	// Read
	public final static String USAGE_READ = "-n #name#";

	public final static String[] HELP_READ = new String[] { "Read variable", };

	public int cmdRead(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("-n")).trim();
		Measurement value = _peer.read(name);
		out.println(value);
		return 0;
	}

	// Write
	public final static String USAGE_WRITE = "-n #name# <value>";

	public final static String[] HELP_WRITE = new String[] { "Write variable", };

	public int cmdWrite(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("-n")).trim();
		double v = Double.parseDouble(((String) opts.get("value")).trim());
		Measurement value = new Measurement(v);
		_peer.write(name, value);
		return 0;
	}

}
