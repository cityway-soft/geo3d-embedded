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
import org.avm.business.tft.TftConfig;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

public class CSSServletImpl extends HttpServlet implements HttpServiceInjector, ConfigurableService {

	private boolean _registered = false;

	private HttpService _http;

	private HttpContext _httpContext;

	private Logger _log;
	
	
	private TftConfig _config;

	public CSSServletImpl (){
		_log = Logger.getInstance(this.getClass());
	}
	
	public void configure(Config config) {
		_config = (TftConfig) config;
	}
	
	public void start() {
		try {
			if (_registered == true)
				return;
			_log.debug(" -- registering " + Tft.ALIAS + "/" + Tft.CSS);
			_log.debug("_http:" + _http);
			_http.registerServlet(Tft.ALIAS + "/" + Tft.CSS, (Servlet) this,
					null, _httpContext);
			_registered = true;
		} catch (Throwable e) {
			_log.error("Error starting zzz", e);
			_registered = false;
		}
	}

	public void setHttpService(HttpService http) {
		_log.debug("set HTTP service !" + http);
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
				if (name.endsWith(".css")) {
					return "text/css";
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

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("body {font: ");
			buf.append(_config.getFontSize());
			buf.append(" ");
			buf.append(_config.getFontName());
			buf.append(";}");
			response.getWriter().print(buf.toString());
		} catch (Throwable t) {
			_log.error("Error doGet :" + t.toString());
			response.getWriter().write("Error doGet:" + t.toString());
			t.printStackTrace(response.getWriter());
		}
	}
	
}
