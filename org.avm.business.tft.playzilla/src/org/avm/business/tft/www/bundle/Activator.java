package org.avm.business.tft.www.bundle;

import org.apache.log4j.Logger;
import org.avm.business.tft.Tft;
import org.avm.business.tft.TftImpl;
import org.avm.business.tft.TftInjector;
import org.avm.business.tft.www.CSSServletImpl;
import org.avm.business.tft.www.HttpServiceInjector;
import org.avm.business.tft.www.TftServlet;
import org.avm.business.tft.www.TftServletImpl;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ManageableService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	static final String PID = TftServlet.class.getName();
	private Logger _log = Logger.getInstance(PID);

	private TftServlet _peer;
	private CSSServletImpl _css;
	private HttpService _http;
	private Tft _tft;
	private ServiceTracker _tracker;
	private BundleContext _context;

	public static final String FILTER = "(|" + "("
			+ org.osgi.framework.Constants.OBJECTCLASS + "="
			+ HttpService.class.getName() + ")" + "("
			+ org.osgi.framework.Constants.OBJECTCLASS + "="
			+ Tft.class.getName() + ")" + ")";

	private Filter _filter;

	public Activator() {
		_peer = new TftServletImpl();
		_css = new CSSServletImpl();
		// _log.setPriority(Priority.DEBUG);
	}

	public void start(BundleContext context) {
		_context = context;
		_peer.setContext(_context);
		_css.setContext(_context);
		try {
			_tracker = new ServiceTracker(_context, getFilter(), this);
			_tracker.open();
			_log.debug("Tracker opened for " + _tracker.size() + " services");
		} catch (Throwable t) {
			_log.error("Error starting "
					+ _context.getBundle().getSymbolicName(), t);
		}
	}

	public void stop(BundleContext context) {
		_context = context;
		try {
			_tracker.close();
			_tracker = null;
			_peer.setContext(_context);
		} catch (Throwable t) {
			_log.error("Error stopping "
					+ _context.getBundle().getSymbolicName(), t);
		}
	}

	private Filter getFilter() {
		_log.debug("Filter = " + FILTER);
		if (_filter == null) {
			try {
				_filter = _context.createFilter(FILTER);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
		return _filter;
	}

	// service
	private void stopService() {
		if (_peer instanceof ManageableService) {
			_log.debug("stopService");
			((ManageableService) _peer).stop();
		}
	}

	private void startService() {
		// le service demarre que si _http ET _tft sont présents.
		if ((_tft == null) || (_http == null))
			return;
		if (_peer instanceof ManageableService) {
			_log.debug("startService");
			((ManageableService) _peer).start();
		}
		_css.start();
	}

	public Object addingService(ServiceReference reference) {
		_log.debug("addingService ...:" + reference);
		Object o = _context.getService(reference);
		_log.debug("..." + o.getClass().getName());

		if (o instanceof Tft) {
			_log.debug("==> Tft");
			_tft = (Tft) o;
			if (_peer instanceof TftInjector) {
				((TftInjector) _peer).setTft(_tft);
			}
			if (_css instanceof HttpServiceInjector) {
				_css.configure((Config)((Tft)_tft).getConfig());
			}
		}
		if (o instanceof HttpService) {
			_log.debug("==> HttpService");
			_http = (HttpService) o;
			if (_peer instanceof HttpServiceInjector) {
				((HttpServiceInjector) _peer).setHttpService(_http);
			}
			if (_css instanceof HttpServiceInjector) {
				((HttpServiceInjector) _css).setHttpService(_http);
			}
			
		}
		startService();
		return o;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		_log.debug("modifiedService ...");
		_log.debug("..." + service.getClass().getName());
		stopService();
		if (service instanceof Tft) {
			_log.debug("==> Tft");
			_tft = (Tft) service;
			if (_peer instanceof TftInjector) {
				((TftInjector) _peer).setTft(_tft);
			}
		}
		if (service instanceof HttpService) {
			_log.debug("==> HttpService");
			_http = (HttpService) service;
			if (_peer instanceof HttpServiceInjector) {
				((HttpServiceInjector) _peer).setHttpService(_http);
			}
			if (_css instanceof HttpServiceInjector) {
				((HttpServiceInjector) _css).setHttpService(_http);
			}
		}
		startService();
	}

	public void removedService(ServiceReference reference, Object service) {
		_log.debug("removedService ...");
		_log.debug("..." + service.getClass().getName());
		stopService();
		if (service instanceof Tft) {
			_log.debug("==> Tft");
			_tft = null;
			if (_peer instanceof TftInjector) {
				((TftInjector) _peer).unsetTft(_tft);
			}
		}
		if (service instanceof HttpService) {
			_log.debug("==> HttpService");
			_http = null;
			if (_peer instanceof HttpServiceInjector) {
				((HttpServiceInjector) _peer).unsetHttpService(_http);
			}
			if (_css instanceof HttpServiceInjector) {
				((HttpServiceInjector) _css).unsetHttpService(_http);
			}
		}
	}

}
