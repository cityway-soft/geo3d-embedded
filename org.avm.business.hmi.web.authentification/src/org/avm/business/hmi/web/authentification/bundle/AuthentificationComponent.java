package org.avm.business.hmi.web.authentification.bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.avm.business.hmi.web.authentification.servlet.AuthentificationServlet;
import org.avm.business.hmi.web.desktop.DesktopUser;
import org.avm.business.hmi.web.utils.HmiWebComponent;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class AuthentificationComponent extends HmiWebComponent implements
		DesktopUser {

	private UserSessionService userSession = null;
	private AuthentificationServlet servlet = null;

	public String getSwitchLabel() {
		// TODO Auto-generated method stub
		return "Prise de poste";
	}

	public int getPriority() {
		// TODO Auto-generated method stub
		return 1;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "authentification";
	}

	public String getDivContent() {
		// TODO Auto-generated method stub
		return page;
	}

	public String getJS() {
		return "js/authentification.js";
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
		userSession = (UserSessionService) context.locateService("userSession");
		servlet = new AuthentificationServlet();
		servlet.setSession(userSession);
	}

	protected void uninit(ComponentContext context) {
	}

	protected String getPageRessource() {
		return "org/avm/business/hmi/web/authentification/servlet/auth.html";
	}

	protected String getServletPath() {
		// TODO Auto-generated method stub
		return "/authentification";
	}

	protected Properties resourcesToRegister() {
		Properties res = new Properties();
		res.put("/desktop/js/authentification.js", "www/js/authentification.js");
		return res;
	}
}
