package org.avm.hmi.swt.management.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Properties;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.hmi.swt.management.Management;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "swt.management";

	private Management _peer;

	public CommandGroupImpl(ComponentContext context, Management peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for SWT Management.");
		_peer = peer;
	}

	// activate a tabbed pane
	public final static String USAGE_ADDTEST = "-k #key# -n #name# -u #up# -d #down# -g #group#";

	public final static String[] HELP_ADDTEST = new String[] { "Add management test button", };

	public int cmdAddtest(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String key = ((String) opts.get("-k")).trim();
		String start = ((String) opts.get("-u")).trim();
		String stop = ((String) opts.get("-d")).trim();
		String group = ((String) opts.get("-g")).trim();
		String name = ((String) opts.get("-n")).trim();

		
		Properties p = new Properties();
		p.put("start", start);
		p.put("stop", stop);
		p.put("name", name);
		p.put("group", group);
		
		System.out.println("[SWT.Management] adding key=" + key + "with:"+p);
		System.out.println("[SWT.Management] config="+_config);
		
		((ConfigImpl)_config).addProperty(key, p);
		return 0;
	}
	
	// activate a tabbed pane
	public final static String USAGE_REMOVETEST = "-k #key#";

	public final static String[] HELP_REMOVETEST = new String[] { "Remove management test button", };

	public int cmdRemovetest(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String key = ((String) opts.get("-k")).trim();
		((ConfigImpl)_config).removeProperty(key);
		return 0;
	}


}
