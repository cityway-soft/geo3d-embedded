package org.avm.business.hmi.web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.avm.elementary.common.AbstractConsumer;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public abstract class HmiWebComponent {

	/**
	 * Used to retrieve content of web page from ressource
	 */
	protected String page;
	/**
	 * Reference to HttpService
	 */
	protected HttpService httpService;
	/**
	 * Abstract consumer, used only if different of null
	 */
	protected AbstractConsumer consumer = null;

	/**
	 * Servlet to register if different of null
	 */
	protected abstract HttpServlet getServlet();

	protected abstract void init(ComponentContext context);

	protected abstract void uninit(ComponentContext context);

	protected abstract String getPageRessource();

	protected abstract String getServletPath();

	protected abstract Properties resourcesToRegister();

	protected void activate(ComponentContext context) {
		loadPage(context);
		init(context);
		if (consumer != null) {
			System.out.println("start consumer");
			consumer.start();
		}
		httpService = (HttpService) context.locateService("httpService");
		if (httpService != null) {
			try {
				HttpServlet servlet = getServlet();
				if (servlet != null) {
					httpService.registerServlet(getServletPath(), servlet,
							null, null);
				}
				registerResources();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamespaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void deactivate(ComponentContext context) {
		if (consumer != null) {
			consumer.stop();
		}
	}

	private void registerResources() throws NamespaceException {
		Properties res = resourcesToRegister();
		if (res != null) {
			Enumeration enum = res.propertyNames();
			while (enum.hasMoreElements()) {
				String elt = (String) enum.nextElement();
				httpService.registerResources(elt, res.getProperty(elt), null);
			}
		}
	}

	private void loadPage(ComponentContext context) {
		URL authUrl = context.getBundleContext().getBundle()
				.getResource(getPageRessource());
		try {
			page = fromStream(authUrl.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String fromStream(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuffer out = new StringBuffer();
		String newLine = System.getProperty("line.separator");
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
			out.append(newLine);
		}
		return out.toString();
	}
}
