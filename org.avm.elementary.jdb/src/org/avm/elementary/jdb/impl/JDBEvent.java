package org.avm.elementary.jdb.impl;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.osgi.util.position.Position;

public class JDBEvent extends org.apache.log4j.spi.LoggingEvent {

	public Position position;
	
	boolean ontime;

	public JDBEvent(Logger logger, boolean ontime, Object message,
			Throwable throwable, Position position) {
		super(logger, Priority.INFO, message, throwable);
		this.ontime = ontime;
		this.position = position;
	}
	
	

}
