package org.avm.elementary.log4j.manager.bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.log4j.manager.Config;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements Config {
	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public void setPattern(String pattern) {
		_config.put(Config.PATTERN_TAG, pattern);
	}

	public String getPattern() {
		String pattern = (String) _config.get(Config.PATTERN_TAG);

		if (pattern == null) {
			pattern = "%r %-5p [%c] %m%n";
		}
		return pattern;
	}

	public void setColored(boolean colored) {
		_config.put(Config.COLORED_TAG, colored ? "true" : "false");
	}

	public boolean getColored() {
		String colored = (String) _config.get(Config.COLORED_TAG);

		if (colored == null) {
			return false;
		}
		return colored.toLowerCase().equals("true");
	}

	public void putCategories(Properties p) {
		String text = save(p);
		_config.put(Config.CATEGORIES_TAG, text);
	}

	public void remove(String index) {
		_config.remove(index);
	}

	public Properties getCategories() {
		String text = (String) _config.get(Config.CATEGORIES_TAG);
		if (text == null) {
			return null;
		}

		return stringToProperties(text);
	}

	private Properties stringToProperties(String text) {
		Properties p = null;
		try {
			p = load(text);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return p;
	}

	public Properties get(String name) {
		String text = (String) _config.get(name);
		if (text == null) {
			return null;
		}
		return stringToProperties(text);
	}

	public Dictionary get() {
		Dictionary d = new Hashtable();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			Properties p = get(key);
			if (p != null) {
				d.put(key, p);
			}
		}
		return d;
	}

	public String toString() {
		StringBuffer text = new StringBuffer();

		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;
			
			if (key.equals(CATEGORIES_TAG)){
			Properties info = get(key);
			if (info != null) {
				text.append(key + ": " + info.toString() + "\n");
			}
			}
			else{
				text.append(key + ": " + _config.get(key) + "\n");
			}
		}
		return text.toString();
	}

	public void setLevel(String level) {
		_config.put(Config.LEVEL_TAG, level);
	}

	public String getLevel() {
		String level = (String) _config.get(Config.LEVEL_TAG);

		if (level == null) {
			return "INFO";
		}
		return level;
	}

	public String getLogRootDir() {
		String rootdir = (String) _config.get(Config.ROOTDIR_TAG);

		if (rootdir == null || rootdir.equals("")) {
			return System.getProperty("org.avm.home") + "/data/upload/";
		}
		return rootdir;
	}
	
	public void setLogRootDir(String rootdir) {
		 _config.put(Config.ROOTDIR_TAG, rootdir);
		
	}

	public Date getEndLogDate() {
		String sEndDate = (String) _config.get(Config.ENDDATE_TAG);
		Date endDate = null;
		if (sEndDate == null || sEndDate.equals("")) {
			return null;
		} else {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			try {
				endDate = df.parse(sEndDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return endDate;
	}
	
	public void setEndLogDate(String date) {
		
		_config.put(Config.ENDDATE_TAG, date);
	}

	public void setFilename(String filename) {
		_config.put(Config.FILENAME_TAG, filename);
		
	}

	public String getFilename() {
		String filename = (String) _config.get(Config.FILENAME_TAG);

		if (filename == null || filename.equals("")) {
			return "avm-{yyyy-MM-dd}.log";
		}
		return filename;
	}

	
}
