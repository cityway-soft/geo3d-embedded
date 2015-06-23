package org.avm.elementary.configurator.bundle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.common.AbstractCommandGroup;
import org.json.JSONException;
import org.json.JSONObject;
import org.knopflerfish.service.console.Session;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "configurator";

	CommandGroupImpl(ComponentContext context, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for all services configurator.");
	}

	// Configure
	public final static String USAGE_CONFIGURE = "<pid> [<keyvalue>] ...";

	public final static String[] HELP_CONFIGURE = new String[] { "<pid> [<key=value>] ..." };

	public int cmdConfigure(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String pid = (String) opts.get("pid");
		String[] keysValues = (String[]) opts.get("keyvalue");

		ConfigurationAdmin cm = (ConfigurationAdmin) _context
				.locateService("cm");
		Configuration configuration = null;
		Dictionary oldDictionary = null;
		try {
			configuration = cm.getConfiguration(pid);
			oldDictionary = configuration.getProperties();
		} catch (IOException e) {
			// LOG!
		}
		if (configuration == null) {
			out.println("No configuration found for pid : " + pid);
			return 1;
		}
		if (keysValues != null) {
			try {
				if (oldDictionary == null) {
					oldDictionary = new Properties();
				}
				String b;
				int firstIndex;
				for (int i = 0; i < keysValues.length; i++) {
					b = keysValues[i];
					firstIndex = b.indexOf('=');
					try {
						oldDictionary.put(b.substring(0, firstIndex).trim(), b
								.substring(firstIndex + 1).trim());
					} catch (Exception e) {
						// LOG!
					}
				}
				configuration.update(oldDictionary);
			} catch (IOException e) {
				// LOG!
			}

		} else {
			if (oldDictionary != null) {
				Enumeration e = oldDictionary.keys();
				Collection list = new ArrayList();
				JSONObject obj = new JSONObject();
				while (e.hasMoreElements()) {
					ConfigItem item = new ConfigItem();
					Object key = e.nextElement();
					if (key.toString().indexOf("service.pid") != -1
							|| key.toString().indexOf("config.date") != -1
							|| key.toString().indexOf("org.avm.config.version") != -1
							|| key.toString().indexOf(
									"org.avm.elementary.dummy.property") != -1) {
						continue;
					}
					Object value = oldDictionary.get(key);
					item.setKey(pid + ":" + key.toString());
					item.setValue(value.toString());
					
					list.add(item);

				}

				try {
					obj.put("configs", list);
					if (session == null) {
						out.print(obj.toString());
					} else {
						out.print(obj.toString(2));
					}

				} catch (JSONException e1) {
					// obj.put("error", e1.getMessage());
					e1.printStackTrace();
				}

			} else {
				out.println("Empty configuration for pid : " + pid);
			}
		}

		return 0;
	}

	public class ConfigItem implements Serializable {
		private static final long serialVersionUID = -8013413186393199L;
		String key;
		String value;

		public ConfigItem() {

		}

		public String getKey() {
			return key;
		}

		public void setKey(String name) {
			this.key = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}
