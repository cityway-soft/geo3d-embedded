package org.avm.business.hmi.web.authentification.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.avm.elementary.useradmin.UserSessionService;

public class AuthentificationServlet extends HttpServlet{

	private UserSessionService session;

	public void setSession(UserSessionService session) {
		this.session = session;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = req.getParameter("action");
		if (action.equals("login")) {
			String login = req.getParameter("login");
			String password = req.getParameter("password");
			session.login(login, password);
		} else if (action.equals("logout")) {
			session.logout();
		}
	}
}
