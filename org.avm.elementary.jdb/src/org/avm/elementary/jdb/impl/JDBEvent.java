package org.avm.elementary.jdb.impl;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.osgi.util.position.Position;

public class JDBEvent extends org.apache.log4j.spi.LoggingEvent {

	public Position position;

	public JDBEvent(Logger logger, Priority priority, Object message,
			Throwable throwable, Position position) {
		super(logger, priority, message, throwable);
		this.position = position;
	}

}
