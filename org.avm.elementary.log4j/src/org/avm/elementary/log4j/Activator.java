package org.avm.elementary.log4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.ColorLayout;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.SyslogAppender;
import org.apache.log4j.WriterAppender;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator,
		org.avm.elementary.log4j.Logger {

	private WriterAppender _console;

	private SyslogAppender _syslog;

	private Layout _layout;

	private ServiceRegistration _registeration;

	public void start(BundleContext context) throws Exception {
		Properties config = configure(context);
		initialize(config);
		_registeration = context.registerService(
				org.avm.elementary.log4j.Logger.class.getName(), this, null);
	}

	public void stop(BundleContext context) throws Exception {
		dispose();
		_registeration.unregister();
	}

	public void disableSyslog() {
		if (_syslog != null) {
			Logger.getRoot().removeAppender(_syslog);
		}
	}

	public void enableSyslog(String bindAddress, String hostAdress) {
		disableSyslog();
		_syslog = new SyslogAppender(_layout, hostAdress,
				SyslogAppender.LOG_USER, bindAddress);
		Logger.getRoot().addAppender(_syslog);
	}

	private Properties configure(BundleContext context) throws IOException {
		URL url = context.getBundle().getResource("config.properties");
		InputStream in = url.openStream();
		Properties config = new Properties();
		config.load(in);
		in.close();
		return config;
	}

	private void initialize(Properties config) {
		String patern = config.getProperty("patern");
		String level = config.getProperty("level");
		boolean colored = Boolean
				.valueOf(config.getProperty("colored", "true")).booleanValue();

		Logger.getRoot().removeAllAppenders();
		Logger root = Logger.getRoot();
		if (colored) {
			_layout = new ColorLayout(patern);
		} else {
			_layout = new PatternLayout(patern);
		}
		_console = new ConsoleAppender(_layout);
		root.addAppender(_console);
		root.setPriority(Priority.toPriority(level));
		root.info("Initialisation du logger");
	}

	private void dispose() {
		Logger root = Logger.getRoot();
		root.removeAllAppenders();
	}

}
