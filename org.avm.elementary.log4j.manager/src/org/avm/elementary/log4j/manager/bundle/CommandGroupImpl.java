package org.avm.elementary.log4j.manager.bundle;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.log4j.manager.Config;
import org.avm.elementary.log4j.manager.Log4jManager;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "log4j.manager";

	private Log4jManager peer;

	CommandGroupImpl(ComponentContext context, Log4jManager peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP, "Log4j settings");

		this.peer = peer;
	}

	// add category & level
	public final static String USAGE_ADDCATEGORY = "<category> [<level>]";

	public final static String[] HELP_ADDCATEGORY = new String[] { "Add category and set level", };

	public int cmdAddcategory(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String category = ((String) opts.get("category")).trim();
		String level = ((String) opts.get("level"));

		Properties p = ((ConfigImpl) _config).get(Config.CATEGORIES_TAG);
		if (p == null) {
			p = new Properties();
		}

		p.put(category, level);

		peer.setCategory(category, level);

		((ConfigImpl) _config).putCategories(p);

		_config.updateConfig();

		out.println("Add '" + category + "' with level :   " + level);
		return 0;
	}

	// remove category
	public final static String USAGE_REMOVECATEGORY = "<category>";

	public final static String[] HELP_REMOVECATEGORY = new String[] { "Remove category", };

	public int cmdRemovecategory(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String category = ((String) opts.get("category")).trim();

		Properties p = ((ConfigImpl) _config).get(Config.CATEGORIES_TAG);
		if (p != null) {
			p.remove(category);
		}

		peer.setCategory(category, "WARN");

		((ConfigImpl) _config).putCategories(p);

		_config.updateConfig();

		out.println("Remove '" + category + "' (level set to WARN)");
		return 0;
	}

	// remove category
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all categories", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Properties p = ((ConfigImpl) _config).get(Config.CATEGORIES_TAG);
		if (p != null && p.isEmpty() == false) {
			Enumeration e = p.keys();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				String level = (String) p.get(name);
				out.println(name + " : " + level);
			}
		} else {
			out.println("No log configuration");
		}

		return 0;
	}

	// set pattern
	public final static String USAGE_SETPATTERN = "[<pattern>]";

	public final static String[] HELP_SETPATTERN = new String[] { "Set log4j pattern", };

	public int cmdSetpattern(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String pattern = ((String) opts.get("pattern"));
		if (pattern != null) {
			((ConfigImpl) _config).setPattern(pattern);
			_config.updateConfig();
		}

		out.println("Pattern:" + ((ConfigImpl) _config).getPattern());

		return 0;
	}

	// set pattern
	public final static String USAGE_SETCOLORED = "[<colored>]";

	public final static String[] HELP_SETCOLORED = new String[] { "Set log4j colored mode", };

	public int cmdSetcolored(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String colored = ((String) opts.get("colored"));
		if (colored != null) {
			((ConfigImpl) _config).setColored(colored.equalsIgnoreCase("true"));
			_config.updateConfig();
		}

		out.println("Colored:" + ((ConfigImpl) _config).getColored());

		return 0;
	}

	// set filename
	public final static String USAGE_SETFILENAME = "[<filename>]";

	public final static String[] HELP_SETFILENAME = new String[] { "Set filename pattern ", };

	public int cmdSetfilename(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String filename = ((String) opts.get("filename"));
		if (filename != null) {
			((ConfigImpl) _config).setFilename(filename);
			_config.updateConfig();
		}

		out.println("Log filename :" + ((ConfigImpl) _config).getFilename());

		return 0;
	}

	// set eraselogs
	public final static String USAGE_ERASELOGS = "";

	public final static String[] HELP_ERASELOGS = new String[] { "Erase all log files", };

	public int cmdEraselogs(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String rootdir = ((ConfigImpl) _config).getLogRootDir();
		int count = -1;
		if (rootdir != null) {
			File dir = new File(rootdir);
			File[] content = dir.listFiles();
			if (content != null) {
				count = content.length;
				for (int i = 0; i < content.length; i++) {
					content[i].delete();
				}
			}
			_config.updateConfig();
		}
		if (count != -1) {
			out.println("#" + count + "file(s) erased.");
		} else {
			out.println("No file to erase (rootdir=" + rootdir + ")");
		}

		return 0;
	}

	// set rootdir
	public final static String USAGE_SETROOTDIR = "[<rootdir>]";

	public final static String[] HELP_SETROOTDIR = new String[] { "Set rootdir", };

	public int cmdSetrootdir(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String rootdir = ((String) opts.get("rootdir"));
		if (rootdir != null) {
			((ConfigImpl) _config).setLogRootDir(rootdir);
			_config.updateConfig();
		}

		out.println("Log rootdir:" + ((ConfigImpl) _config).getLogRootDir());

		return 0;
	}

	// set rootdir
	public final static String USAGE_SETENDDATE = "[<enddate>]";

	public final static String[] HELP_SETENDDATE = new String[] { "Set end log date yyyy-MM-dd", };

	public int cmdSetenddate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String endDate = ((String) opts.get("enddate"));
		if (endDate != null) {
			((ConfigImpl) _config).setEndLogDate(endDate);
			_config.updateConfig();
		}

		out.println("End date:" + ((ConfigImpl) _config).getEndLogDate());

		return 0;
	}

}
