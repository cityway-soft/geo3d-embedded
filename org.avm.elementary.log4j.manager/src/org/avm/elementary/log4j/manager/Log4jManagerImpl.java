package org.avm.elementary.log4j.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.ColorLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.log4j.manager.bundle.ConfigImpl;

public class Log4jManagerImpl implements Log4jManager, ConfigurableService,
		ManageableService {


	public void setCategory(String name, String level) {

		Logger _log = Logger.getInstance(name);
		_log.setPriority(Priority.toPriority(level, Priority.DEBUG));
	}

	public void configure(Config config) {
		if (config != null) {
			ConfigImpl c = (ConfigImpl) config;
			
			// -- rootdir
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat
					.format(c.getLogRootDir(), arguments);
			File logDir = new File(text);
			String rootDir = logDir.getAbsolutePath();

			if (logDir.exists() == false) {
				logDir.mkdirs();
			}


			// -- compress & move old log files
			purgeLogFiles(logDir);
			
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
			}else{
				Logger.getRoot().info("File logger end ("+endDate+").");
			}
			
			

		}

	}
	
	private void purgeLogFiles(File logDir){
		String destination = System.getProperty("org.avm.home")
				+ "/data/upload/";
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		final long today = cal.getTime().getTime();

		if (logDir.exists()) {
			File[] list = logDir.listFiles(new FilenameFilter() {

				public boolean accept(File dir, String filename) {
					File file = new File(dir.getAbsolutePath() + "/" +filename);
					boolean result = (filename.endsWith(".log") && file.lastModified() < today);
					return result;
				}
			});
			if (list != null && list.length > 0) {
				for (int i = 0; i < list.length; i++) {
					try {
						compressAndMove(list[i], destination);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	public void start() {


	}

	private void compressAndMove(File logFile, String destdir)
			throws IOException {
		FileInputStream in = new FileInputStream(logFile);

		String compressedFilename = destdir + logFile.getName() + ".gz";
		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
				compressedFilename));
		byte[] buff = new byte[1024 * 4];
		int c;

		while ((c = in.read(buff)) > 0) {
			out.write(buff, 0, c);
		}

		out.close();
		logFile.delete();
		Logger.getRoot().info("Move and compress " + logFile.getAbsolutePath());
	}

	public void stop() {

	}
}
