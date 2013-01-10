package org.avm.elementary.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class OsgiLogAppender extends AppenderSkeleton implements
		ServiceTrackerCustomizer {

	private BundleContext _bc;

	private ServiceTracker _tracker;

	private LogService _logger;

	public OsgiLogAppender(BundleContext bc) {
		super();
		_bc = bc;
		_tracker = new ServiceTracker(bc, LogService.class.getName(), this);
		_tracker.open();
	}

	protected void append(LoggingEvent event) {
		if (_logger != null) {
			int level = getPriority(event.priority);
			String message = layout.format(event);
			_logger.log(level, message);
		}
	}

	public void close() {
		if (_tracker != null) {
			_tracker.close();
			_tracker = null;
		}
	}

	public Object addingService(ServiceReference sr) {
		_logger = (LogService) _bc.getService(sr);
		return _logger;
	}

	public void modifiedService(ServiceReference sr, Object o) {

	}

	public void removedService(ServiceReference sr, Object o) {
		_bc.ungetService(sr);
		_logger = null;
	}

	private int getPriority(Priority level) {
		int result = LogService.LOG_DEBUG;

		switch (level.toInt()) {
		case Priority.DEBUG_INT:
			result = LogService.LOG_DEBUG;
			break;
		case Priority.INFO_INT:
			result = LogService.LOG_INFO;
			break;
		case Priority.WARN_INT:
			result = LogService.LOG_WARNING;
			break;
		case Priority.ERROR_INT:
			result = LogService.LOG_ERROR;
			break;
		case Priority.ALL_INT:
			result = LogService.LOG_ERROR;
			break;
		}

		return result;
	}

}
