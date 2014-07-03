package org.avm.business.hmi.web.authentification.bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;

import org.avm.business.hmi.web.authentification.servlet.AuthentificationServlet;
import org.avm.business.hmi.web.desktop.DesktopUser;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class AuthentificationComponent implements DesktopUser {

	private String page = "test";
	private HttpService httpService = null;
	private UserSessionService userSession = null;
	private AuthentificationServlet servlet = null;


	public void activate(ComponentContext context) {
		URL authUrl = context
				.getBundleContext()
				.getBundle()
				.getResource(
						"org/avm/business/hmi/web/authentification/servlet/auth.html");
		try {
			page = fromStream(authUrl.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpService = (HttpService) context.locateService("httpService");
		userSession = (UserSessionService) context.locateService("userSession");
		if (httpService != null && userSession != null) {
			servlet = new AuthentificationServlet();
			
			servlet.setSession(userSession);
			try {
				httpService.registerServlet("/authentification", servlet, null,
						null);
				httpService.registerResources("/desktop/js/authentification.js", "www/js/authentification.js", null);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamespaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void deactivate(ComponentContext context) {

	}

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
}
