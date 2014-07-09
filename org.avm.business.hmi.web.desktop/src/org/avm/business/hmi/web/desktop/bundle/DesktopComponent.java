package org.avm.business.hmi.web.desktop.bundle;

import java.net.URL;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.avm.business.hmi.web.desktop.DesktopInfosUser;
import org.avm.business.hmi.web.desktop.DesktopUser;
import org.avm.business.hmi.web.desktop.servlet.DesktopServlet;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class DesktopComponent {
	private Logger log = Logger.getInstance(DesktopComponent.class.getName());
	private HttpService httpService;
	private DesktopServlet desktopServlet = new DesktopServlet();

	public void activate(ComponentContext context) {
		httpService = (HttpService) context.locateService("httpService");
		URL url = context
				.getBundleContext()
				.getBundle()
				.getResource(
						"org/avm/business/hmi/web/desktop/servlet/index.html");
		desktopServlet.setIndexURL(url);
		if (httpService != null) {
			log.info("//////////////// register");
			try {
				httpService.registerResources("/desktop", "/www", null);
				httpService.registerServlet("/desktop/index.html",
						desktopServlet, null, null);
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
	
	public void addDesktopUser(DesktopUser desktopUser){
		desktopServlet.addDesktopUser(desktopUser);
	}
	
	public void removeDesktopUser(DesktopUser desktopUser){
		desktopServlet.removeDesktopUser(desktopUser);
	}
	
	public void addDesktopInfosUser(DesktopInfosUser desktopInfosUser){
		desktopServlet.addDesktopInfosUser(desktopInfosUser);
	}
	
	public void removeDesktopInfosUser(DesktopInfosUser desktopInfosUser){
		desktopServlet.removeDesktopInfosUser(desktopInfosUser);
	}
}
