package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;

public class ColorLayout extends PatternLayout {

	public ColorLayout() {
		super();
	}

	public ColorLayout(String pattern) {
		super(pattern);
	}

	public String format(LoggingEvent event) {
		if (event.priority.isGreaterOrEqual(Priority.WARN)) {
			if (event.priority.isGreaterOrEqual(Priority.ERROR)) {
				event.message = org.avm.elementary.log4j.Constants.SETCOLOR_FAILURE
						+ event.message
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL;
			} else {
				event.message = org.avm.elementary.log4j.Constants.SETCOLOR_WARNING
						+ event.message
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL;
			}
		}

		return super.format(event);
	}
}
