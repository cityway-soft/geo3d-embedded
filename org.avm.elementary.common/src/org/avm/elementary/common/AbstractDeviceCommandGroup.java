package org.avm.elementary.common;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class AbstractDeviceCommandGroup extends AbstractCommandGroup {

	public AbstractDeviceCommandGroup(ComponentContext context,
			AbstractConfig config, String group) {
		super(context, config, group, "Configuration commands for the " + group);
	}

	// driver category
	public final static String USAGE_SETCATEGORY = "<category>";

	public final static String[] HELP_SETCATEGORY = new String[] { "Set driver category", };

	public int cmdSetcategory(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String category = ((String) opts.get("category")).trim();
		((DeviceConfig) _config).setCategory(category);
		_config.updateConfig();

		cmdShowcategory(opts, in, out, session);

		return 0;
	}

	public final static String USAGE_SHOWCATEGORY = "";

	public final static String[] HELP_SHOWCATEGORY = new String[] { "Show driver category", };

	public int cmdShowcategory(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current driver category : "
				+ ((DeviceConfig) _config).getCategory());
		return 0;
	}

	// driver description
	public final static String USAGE_SETDESCRIPTION = "<description>";

	public final static String[] HELP_SETDESCRIPTION = new String[] { "Set driver description", };

	public int cmdSetdescription(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String category = ((String) opts.get("description")).trim();
		((DeviceConfig) _config).setDescription(category);
		_config.updateConfig();

		cmdShowdescription(opts, in, out, session);
		return 0;
	}

	public final static String USAGE_SHOWDESCRIPTION = "";

	public final static String[] HELP_SHOWDESCRIPTION = new String[] { "Show driver description", };

	public int cmdShowdescription(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current driver description : "
				+ ((DeviceConfig) _config).getCategory());
		return 0;
	}

	// driver manufacturer
	public final static String USAGE_SETMANUFACTURER = "<manufacturer>";

	public final static String[] HELP_SETMANUFACTURER = new String[] { "Set driver manufacturer", };

	public int cmdSetmanufacturer(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String manufacturer = ((String) opts.get("manufacturer")).trim();
		((DeviceConfig) _config).setManufacturer(manufacturer);
		//_config.updateConfig();

		cmdShowmanufacturer(opts, in, out, session);
		return 0;
	}

	public final static String USAGE_SHOWMANUFACTURER = "";

	public final static String[] HELP_SHOWMANUFACTURER = new String[] { "Show driver manufacturer", };

	public int cmdShowmanufacturer(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current driver manufacturer : "
				+ ((DeviceConfig) _config).getManufacturer());
		return 0;
	}

	// driver model
	public final static String USAGE_SETMODEL = "<model>";

	public final static String[] HELP_SETMODEL = new String[] { "Set driver model", };

	public int cmdSetmodel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String model = ((String) opts.get("model")).trim();
		((DeviceConfig) _config).setModel(model);
		//_config.updateConfig();

		cmdShowmodel(opts, in, out, session);
		return 0;
	}

	public final static String USAGE_SHOWMODEL = "";

	public final static String[] HELP_SHOWMODEL = new String[] { "Show driver model", };

	public int cmdShowmodel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current driver model : "
				+ ((DeviceConfig) _config).getModel());
		return 0;
	}

	// driver name
	public final static String USAGE_SETNAME = "<name>";

	public final static String[] HELP_SETNAME = new String[] { "Set driver name", };

	public int cmdSetname(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String name = ((String) opts.get("name")).trim();
		((DeviceConfig) _config).setName(name);
		//_config.updateConfig();

		cmdShowname(opts, in, out, session);
		return 0;
	}

	public final static String USAGE_SHOWNAME = "";

	public final static String[] HELP_SHOWNAME = new String[] { "Show driver name", };

	public int cmdShowname(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current driver name : "
				+ ((DeviceConfig) _config).getName());
		return 0;
	}

	// driver serial
	public final static String USAGE_SETSERIAL = "<serial>";

	public final static String[] HELP_SETSERIAL = new String[] { "Set driver serial", };

	public int cmdSetserial(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String serial = ((String) opts.get("serial")).trim();
		((DeviceConfig) _config).setSerial(serial);
		//_config.updateConfig();

		cmdShowserial(opts, in, out, session);
		return 0;
	}

	public final static String USAGE_SHOWSERIAL = "";

	public final static String[] HELP_SHOWSERIAL = new String[] { "Show driver serial", };

	public int cmdShowserial(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current driver serial : "
				+ ((DeviceConfig) _config).getSerial());
		return 0;
	}

	// driver version
	public final static String USAGE_SETVERSION = "<version>";

	public final static String[] HELP_SETVERSION = new String[] { "Set driver version", };

	public int cmdSetversion(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String version = ((String) opts.get("version")).trim();
		((DeviceConfig) _config).setVersion(version);
		//_config.updateConfig();

		cmdShowversion(opts, in, out, session);
		return 0;
	}

	public final static String USAGE_SHOWVERSION = "";

	public final static String[] HELP_SHOWVERSION = new String[] { "Show driver version", };

	public int cmdShowversion(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current driver version : "
				+ ((DeviceConfig) _config).getVersion());
		return 0;
	}

	// parameters
	public final static String USAGE_SETPARAMETERS = "<key> <value>";

	public final static String[] HELP_SETPARAMETERS = new String[] { "Set parameters", };

	public int cmdSetparameters(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String key = ((String) opts.get("key")).trim();
		String value = ((String) opts.get("value")).trim();
		((DeviceConfig) _config).setParameters(key, value);
		//_config.updateConfig();

		cmdShowparameters(opts, in, out, session);
		return 0;
	}

	public final static String USAGE_SHOWPARAMETERS = "<key>";

	public final static String[] HELP_SHOWPARAMETERS = new String[] { "Show parameters", };

	public int cmdShowparameters(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String key = ((String) opts.get("key")).trim();
		out.println(key + " : " + ((DeviceConfig) _config).getParamerter(key));
		return 0;
	}
}
