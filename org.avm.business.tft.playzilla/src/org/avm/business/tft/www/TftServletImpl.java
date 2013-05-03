package org.avm.business.tft.www;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.avm.business.tft.Tft;
import org.avm.business.tft.TftInjector;
import org.avm.elementary.common.ManageableService;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

public class TftServletImpl extends HttpServlet implements TftServlet,
		TftInjector, HttpServiceInjector, ManageableService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -691612481497565889L;

	private Logger _log;

	private HttpService _http;

	static private Tft _tft;

	private HttpContext _httpContext;

	private boolean _registered;

	public TftServletImpl() {
		super();
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			_log.debug("doGet(" + request.getQueryString() + ",...)");
			_tft.process(request, response);
		} catch (Throwable t) {
			_log.error("Error doGet :" + t.toString());
			response.getWriter().write("Error doGet:" + t.toString());
			t.printStackTrace(response.getWriter());
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		_log.debug("doPost(" + request.getQueryString() + ",...)");
	}

	public void start() {
		_log.debug("start");
		try {
			if (_registered == true)
				return;
			_log.debug(" -- registering " + Tft.ALIAS);
			_http.registerResources(Tft.ALIAS, Tft.NAME, _httpContext);
			_http.registerServlet(Tft.ALIAS + "/" + Tft.SERVLET,
					(Servlet) this, null, _httpContext);
			_registered = true;
		} catch (Throwable e) {
			_log.error("Error starting", e);
			_registered = false;
		}
	}

	public void stop() {
		_log.debug("stop");
		if (_http == null)
			return;
		if (_registered == false)
			return;
		try {
			_log.debug(" -- unregistering " + Tft.ALIAS);
			_http.unregister(Tft.ALIAS);
			_http.unregister(Tft.ALIAS + "/" + Tft.SERVLET);
			_registered = false;
		} catch (Throwable e) {
			_log.error("Error stopping", e);
		}
	}

	public void setTft(Tft tft) {
		_log.debug("setTft");
		_tft = tft;
		_tft.refresh();
	}

	public void unsetTft(Tft tft) {
		_tft = null;
	}

	public void setHttpService(HttpService http) {
		_http = http;
	}

	public void unsetHttpService(HttpService http) {
		_http = null;
	}

	public void setContext(final BundleContext context) {
		_log.debug("setContext(" + context + ")");

		_httpContext = new HttpContext() {
			private BundleContext _bcontext = context;

			
			public String getMimeType(String name) {
				_log.debug("getMimeType(" + name + ")");
				if (name.endsWith(".xhtml")) {
					return "application/xhtml+xml";
				} else if (name.endsWith(".svg")) {
					return "image/svg+xml";
				} 
				return null;
			}

			public boolean handleSecurity(HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				return true;
			}

			public URL getResource(String name) {
				if (_bcontext.getBundle().getResource(name) == null) {
					if (name.endsWith("index.html")) {
						int index = name.indexOf("index");
						String start = name.substring(0, index);
						name = start + "cat22.html";
					}
				}
				if (_log.isDebugEnabled()) {
					_log.info("=> getResource(" + name + ") = "
							+ _bcontext.getBundle().getResource(name));
				}
				URL result = _bcontext.getBundle().getResource(name);
				return result;

			}
		};
		
	}
}
