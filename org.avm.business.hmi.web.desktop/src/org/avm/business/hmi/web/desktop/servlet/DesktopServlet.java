package org.avm.business.hmi.web.desktop.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.avm.business.hmi.web.desktop.DesktopUser;
import org.osgi.framework.BundleContext;

public class DesktopServlet extends HttpServlet {

	private URL indexURL;
	private List desktopUsers = new ArrayList();

	public URL getIndexURL() {
		return indexURL;
	}

	public void setIndexURL(URL indexURL) {
		this.indexURL = indexURL;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (indexURL != null) {
			InputStream is = indexURL.openStream();
			OutputStream os = resp.getOutputStream();
			String page = fromStream(is);
			page = replaceOnce(page, "$SWITCH", getSwitches());
			page = replaceOnce(page, "$CONTENT", getContent());
			page = replaceOnce(page, "$JS", getJSs());
			os.write(page.getBytes());
			os.flush();
		}
	}

	private String getContent() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < desktopUsers.size(); ++i) {
			DesktopUser elt = (DesktopUser) desktopUsers.get(i);
			sb.append("<div id=\"");
			sb.append(elt.getName());
			sb.append("\" class=\"activity\">");
			String tmp = elt.getDivContent();
			sb.append((elt != null) ? tmp : "");
			sb.append("</div>");
		}
		return sb.toString();
	}

	private String getSwitches() {
		StringBuffer sb = new StringBuffer();
		sb.append("<ul>");
		for (int i = 0; i < desktopUsers.size(); ++i) {
			DesktopUser elt = (DesktopUser) desktopUsers.get(i);
			sb.append("<li><a href=\"#\" ");
			sb.append("onclick=\"showActivity('#");
			sb.append(elt.getName());
			sb.append("');\">");
			sb.append(elt.getSwitchLabel());
			sb.append("</a></li>");
		}
		sb.append("</ul>");
		return sb.toString();
	}

	private String getJSs() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < desktopUsers.size(); ++i) {
			DesktopUser elt = (DesktopUser) desktopUsers.get(i);
			String js = elt.getJS();
			if (js != null && !js.equals("")) {
				sb.append("<script src=\"");
				sb.append(js);
				sb.append("\"></script>");
			}
		}
		return sb.toString();
	}

	private String replaceOnce(String content, String old, String newStr) {
		int i = content.indexOf(old);
		if (i == -1) {
			return content;
		}
		StringBuffer ret = new StringBuffer();
		ret.append(content.substring(0, i));
		ret.append(newStr);
		ret.append(content.substring(i + old.length()));
		return ret.toString();
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

	public void addDesktopUser(DesktopUser desktopUser) {
		desktopUsers.add(desktopUser);
	}

	public void removeDesktopUser(DesktopUser desktopUser) {
		desktopUsers.remove(desktopUser);
	}

}
