package org.avm.business.hmi.web.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stringtree.json.JSONWriter;

public class HmiServlet extends HttpServlet {

	protected boolean isNoWait(HttpServletRequest req) {
		String nowait = req.getParameter("nowait");
		boolean wait = (nowait != null && nowait.equals("true")) ? true : false;
		return wait;
	}

	protected void sendJSON(HttpServletResponse resp, Object obj)
			throws IOException {
		JSONWriter writer = new JSONWriter();
		String text = writer.write(obj);
		resp.setContentType("application/json");
		resp.getOutputStream().write(text.getBytes());
	}
	
	protected int getInt(String val) {
		int ret = -1;
		try {
			ret = Integer.parseInt(val);
		} catch (NumberFormatException nfe) {

		}
		return ret;
	}
}
