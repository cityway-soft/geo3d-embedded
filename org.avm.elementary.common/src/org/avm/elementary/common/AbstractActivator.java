package org.avm.elementary.common;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

public abstract class AbstractActivator {

	protected Logger _log = Logger.getInstance(this.getClass());

	protected ComponentContext _context;
	
	protected boolean _started = false;

	public AbstractActivator() {
		super();
	}

	protected abstract void start(ComponentContext context);

	protected abstract void stop(ComponentContext context);

	public void activate(ComponentContext context) throws Exception {
		_log.info("Components activating :" + context);
		_context = context;
		try {
			start(context);
		} catch (Throwable e) {
			_log.error(e);
		} finally {
			_started = true;
			_log.info("Components activated");
		}
	}

	public boolean isStarted() {
		return _started;
	}

	public void deactivate(ComponentContext context) {
		_log.info("Component deactivating");
		try {
			stop(context);
		} catch (Throwable e) {
			_log.error(e);
		} finally {
			_context = null;
			_log.info("Component deactivated");
		}
	}

	public String getPid() {
		if (_context != null) {
			return (String) _context.getProperties().get("service.pid");
		}
		return null;
	}

	public ComponentContext getContext() {
		return _context;
	}

	public Logger getLogger() {
		return _log;
	}

}
