package org.avm.elementary.log4j.manager;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.ColorLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.log4j.manager.bundle.ConfigImpl;

public class Log4jManagerImpl implements Log4jManager, ConfigurableService {

	public void setCategory(String name, String level) {

		Logger _log = Logger.getInstance(name);
		_log.setPriority(Priority.toPriority(level, Priority.DEBUG));
	}

	public void configure(Config config) {
		if (config != null) {
			ConfigImpl c = (ConfigImpl) config;
			
			// -- check end log date
			Date endDate = c.getEndLogDate();
			if (endDate == null) {
				// -- pas de log
				Logger.getRoot().info("File logger disabled.");
				return;
			}

			Date today = new Date();
			Calendar end = Calendar.getInstance();
			end.add(Calendar.DAY_OF_MONTH, 1);
			if (today.before(end.getTime())) {

				Properties p = c
						.get(org.avm.elementary.log4j.manager.Config.CATEGORIES_TAG);
				if (p != null) {
					Enumeration e = p.keys();
					while (e.hasMoreElements()) {

						String name = (String) e.nextElement();
						String level = (String) p.get(name);

						setCategory(name, level);
					}
				}

				// -- pattern
				String pattern = c.getPattern();

				
				// -- level
				String level = c.getLevel();

				// -- rootdir
				Object[] arguments = { System.getProperty("org.avm.home") };
				String text = MessageFormat
						.format(c.getLogRootDir(), arguments);
				File fd = new File(text);
				String rootDir = fd.getAbsolutePath();

				String filename = rootDir + "/" + c.getFilename();

				// -- colored logs
				boolean colored = c.getColored();

				Logger.getRoot().removeAllAppenders();
				Logger root = Logger.getRoot();
				Layout layout;
				if (colored) {
					layout = new ColorLayout(pattern);
				} else {
					layout = new PatternLayout(pattern);
				}
				RollingFileAppender appender;
				try {
					appender = new RollingFileAppender(layout, filename);
					appender.setName(org.avm.elementary.log4j.manager.Config.APPENDER_NAME);
					root.addAppender(appender);
					root.setPriority(Priority.toPriority(level));
					root.info("File Logger initialized.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
}
