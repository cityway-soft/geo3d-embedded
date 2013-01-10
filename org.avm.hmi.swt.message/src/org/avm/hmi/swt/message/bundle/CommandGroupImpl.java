package org.avm.hmi.swt.message.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.hmi.swt.desktop.MessageBox;
import org.avm.hmi.swt.message.MessageConfig;
import org.avm.hmi.swt.message.MessageIhm;
import org.avm.hmi.swt.message.MessageImpl;
import org.eclipse.swt.SWT;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "swt.message";

	private MessageIhm _peer;

	CommandGroupImpl(ComponentContext context, MessageIhm peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for user message.");
		_peer = peer;
	}


	// Remove
	public final static String USAGE_REMOVEPREDEFINEDMESSAGE = "<name>";

	public final static String[] HELP_REMOVEPREDEFINEDMESSAGE = new String[] { "Remove properties", };

	public int cmdRemovepredefinedmessage(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("name")).trim();
		Properties props = (Properties)((MessageConfig) _config).getProperty(MessageConfig.MESSAGES);
		props.remove(name);
		((ConfigImpl) _config).addProperty(MessageConfig.MESSAGES, props);
		_config.updateConfig(false);
		return 0;
	}

	// List
	public final static String USAGE_LISTPREDEFINEDMESSAGE = "";

	public final static String[] HELP_LISTPREDEFINEDMESSAGE = new String[] { "List all properties", };

	public int cmdListpredefinedmessage(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Properties props = (Properties)((MessageConfig) _config).getProperty(MessageConfig.MESSAGES);
		Enumeration e = props.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			out.println(key + " - " + props.getProperty(key));
			
		}
		return 0;
	}
	
	
	// Add
	public final static String USAGE_ADDPREDEFINEDMESSAGE = "<title> <message>";

	public final static String[] HELP_ADDPREDEFINEDMESSAGE = new String[] { "Add predefined message", };

	public int cmdAddpredefinedmessage(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String title = ((String) opts.get("title")).trim();
		String message = ((String) opts.get("message")).trim();

		Properties properties = (Properties)((MessageConfig) _config).getProperty(MessageConfig.MESSAGES);
		if (properties==null){
			properties = new Properties();
		}
		properties.put(title, message);

		((MessageImpl)_peer).addPredefinedMessag(title, message);
		((ConfigImpl) _config).addProperty(MessageConfig.MESSAGES, properties);
		_config.updateConfig(false);
		return 0;
	}
	
	// Print
	public final static String USAGE_PRINT = "[-t #title#] [<message>]";

	public final static String[] HELP_PRINT = new String[] { "Add predefined message", };

	public int cmdPrint(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String title = ((String) opts.get("-t"));
		String message = ((String) opts.get("message"));


		MessageBox.setMessage(title, message, MessageBox.MESSAGE_WARNING, SWT.CENTER);
		
		return 0;
	}
	
}
