package org.avm.business.hmi.web.avm.bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.avm.business.core.Avm;
import org.avm.business.hmi.web.avm.servlet.AvmServlet;
import org.avm.business.hmi.web.desktop.DesktopUser;
import org.avm.business.hmi.web.utils.HmiWebComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class AvmComponent extends HmiWebComponent implements DesktopUser {

	private AvmServlet servlet;
	
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

	protected HttpServlet getServlet() {
		return servlet;
	}

	protected void init(ComponentContext context) {
		servlet = new AvmServlet();
		consumer = new ConsumerImpl(context, servlet);
	}

	protected void uninit(ComponentContext context) {

	}

	protected String getPageRessource() {
		return "org/avm/business/hmi/web/avm/servlet/avm.html";
	}

	protected String getServletPath() {
		return "/avm";
	}

	protected Properties resourcesToRegister() {
		Properties res = new Properties();
		res.put("/desktop/js/avm.js", "www/js/avm.js");
		return res;
	}

}
