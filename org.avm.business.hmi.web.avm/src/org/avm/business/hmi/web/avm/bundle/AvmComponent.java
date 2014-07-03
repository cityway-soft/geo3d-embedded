package org.avm.business.hmi.web.avm.bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;

import org.avm.business.core.Avm;
import org.avm.business.hmi.web.avm.servlet.AvmServlet;
import org.avm.business.hmi.web.desktop.DesktopUser;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class AvmComponent implements DesktopUser {

	private AvmServlet servlet;
	private HttpService httpService;
	private String page="SAE";
	private ConsumerImpl consumer;

	public void activate(ComponentContext context) {
		servlet = new AvmServlet();
		consumer = new ConsumerImpl(context, servlet);
		consumer.start();
		httpService = (HttpService) context.locateService("httpService");
		loadPage(context);
		if (httpService != null) {
			try {
				httpService.registerServlet("/avm", servlet, null, null);
				httpService.registerResources("/desktop/js/avm.js", "www/js/avm.js", null);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamespaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void loadPage(ComponentContext context){
		URL authUrl = context
				.getBundleContext()
				.getBundle()
				.getResource(
						"org/avm/business/hmi/web/avm/servlet/avm.html");
		try {
			page = fromStream(authUrl.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deactivate(ComponentContext context) {
		consumer.stop();
	}

	public void setAvm(Avm avm) {
		servlet.setAvm(avm);
	}

	public void unsetAvm(Avm avm) {
		servlet.setAvm(null);
	}

	public String getSwitchLabel() {
		// TODO Auto-generated method stub
		return "SAE";
	}

	public int getPriority() {
		// TODO Auto-generated method stub
		return 1;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "avm";
	}

	public String getDivContent() {
		// TODO Auto-generated method stub
		return page;
	}

	public String getJS() {
		// TODO Auto-generated method stub
		return "js/avm.js";
	}
	
	public static String fromStream(InputStream in) throws IOException {
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
