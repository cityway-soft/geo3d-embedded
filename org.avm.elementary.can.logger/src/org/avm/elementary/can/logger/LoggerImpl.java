package org.avm.elementary.can.logger;

import java.io.IOException;
import java.text.MessageFormat;

import org.apache.log4j.Priority;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;

public class LoggerImpl implements Logger, ConfigurableService,
		ConsumerService, ManageableService {

	private String BASE_CATEGORY;

	private LoggerConfig _config;
	private CanLayout _layout;
	private CanAppender _appender;

	public void configure(Config config) {
		_config = (LoggerConfig) config;
		BASE_CATEGORY = _config.getCanServicePid();
	}

	public void start() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger
				.getInstance(BASE_CATEGORY);
		logger.removeAllAppenders();
		logger.setAdditivity(false);
		_layout = new CanLayout();
		try {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat
					.format(_config.getFilename(), arguments);
			_appender = new CanAppender(_layout, text, ".E");
			_appender.setSize(1024);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		logger.addAppender(_appender);
	}

	public void stop() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger
				.getInstance(BASE_CATEGORY);
		_appender.close();
		logger.removeAppender(_appender);
	}

	public void notify(Object o) {
		if (o instanceof PGN) {
			PGN pgn = (PGN) o;
			journalize(pgn);
			pgn.dispose();
		}
	}

	public void journalize(PGN pgn) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger
				.getInstance(BASE_CATEGORY);
		logger
				.callAppenders(new CanEvent(logger, Priority.ALL, null, null,
						pgn));
	}

}
