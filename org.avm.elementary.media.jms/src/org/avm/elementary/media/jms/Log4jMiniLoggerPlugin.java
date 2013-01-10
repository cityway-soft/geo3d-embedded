package org.avm.elementary.media.jms;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jboss.logging.LoggerPlugin;

public class Log4jMiniLoggerPlugin implements LoggerPlugin {
	private transient org.apache.log4j.Logger log;

	public void init(String name) {
		log = Logger.getInstance(name);
		log.debug("Initialisation Log4jMiniLoggerPlugin");
	}

	public boolean isTraceEnabled() {
		return log.isEnabledFor(Priority.ALL);
	}

	public void trace(Object message) {
		log.info(message);
	}

	public void trace(Object message, Throwable t) {
		log.info(message, t);
	}

	public boolean isDebugEnabled() {
		return log.isEnabledFor(Priority.DEBUG);
	}

	public void debug(Object message) {
		log.debug(message);
	}

	public void debug(Object message, Throwable t) {
		log.debug(message, t);
	}

	public boolean isInfoEnabled() {
		return log.isEnabledFor(Priority.INFO);
	}

	public void info(Object message) {
		log.info(message);
	}

	public void info(Object message, Throwable t) {
		log.info(message, t);
	}

	public void warn(Object message) {
		log.warn(message);
	}

	public void warn(Object message, Throwable t) {
		log.warn(message, t);
	}

	public void error(Object message) {
		log.error(message);
	}

	public void error(Object message, Throwable t) {
		log.error(message, t);
	}

	public void fatal(Object message) {
		log.fatal(message);
	}

	public void fatal(Object message, Throwable t) {
		log.fatal(message, t);
	}

}
