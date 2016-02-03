package org.avm.elementary.jdb.impl;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.avm.device.gps.Gps;
import org.avm.device.gps.GpsInjector;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.position.Position;

public class JDBImpl implements ConfigurableService, ManageableService, JDB,
		GpsInjector {

	public static final String BASE_CATEGORY = "jdb";

	public static final String FILENAME = "jdb.txt";

	private Logger _log = Logger.getInstance(this.getClass());

	private JDBConfig _config;

	private Layout _layout;

	private JDBAppender _appender;
	
	private Gps _gps;

	public JDBImpl() {

	}

	public void configure(Config config) {
		_config = (JDBConfig) config;
		if (_config != null) {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String dir = MessageFormat.format(_config.getFilename(), arguments);
			File jdbDir = new File(dir);
			jdbDir = jdbDir.getParentFile();
			if (jdbDir.exists() == false) {
				jdbDir.mkdir();
			}
		}
	}

	public void setGps(Gps gps) {
		_gps = gps;
	}

	public void unsetGps(Gps gps) {
		_gps = null;
	}

	public void start() {

		Logger logger = Logger.getInstance(BASE_CATEGORY);
		logger.removeAllAppenders();
		logger.setAdditivity(false);
		_layout = new JDBLayout();
		try {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat
					.format(_config.getFilename(), arguments);
			_appender = new JDBAppender(_layout, text, _config.getPattern(), true);
			_appender.setSize(_config.getSize());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		logger.addAppender(_appender);

	}

	public void stop() {
		Logger logger = Logger.getInstance(BASE_CATEGORY);
		_appender.close();
		logger.removeAppender(_appender);
	}

	public void journalize(String category, String message) {
		boolean onTime = org.avm.device.plateform.System.isOnTime();
		Logger logger = Logger.getInstance(BASE_CATEGORY + "." + category);
		Position position = null;
		if (_gps != null) {
			position = _gps.getCurrentPosition();
		}
		logger.callAppenders(new JDBEvent(logger, onTime, message, null,
				position));
	}

	public void sync() {
		_appender.flush();
	}

	public int getCheckPeriod() {
		return _appender.getCheckPeriod();
	}

	public String getScheduledFilename(Date date) {
		return _appender.getScheduledFilename(date);
	}

	public String getRootPath() {
		Object[] arguments = { System.getProperty("org.avm.home") };
		String text = MessageFormat.format(_config.getFilename(), arguments);
		File fd = new File(text);
		int index = fd.getAbsolutePath().lastIndexOf(File.separatorChar);
		return fd.getAbsolutePath().substring(0, index);
	}
}