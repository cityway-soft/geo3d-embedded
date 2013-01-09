package org.avm.elementay.can.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.can.parser.PGN;

public class CanEvent extends org.apache.log4j.spi.LoggingEvent {

	public PGN pgn;

	public CanEvent(Logger logger, Priority priority, Object message,
			Throwable throwable, PGN pgn) {
		super(logger, priority, message, throwable);
		this.pgn = pgn;
	}

}
