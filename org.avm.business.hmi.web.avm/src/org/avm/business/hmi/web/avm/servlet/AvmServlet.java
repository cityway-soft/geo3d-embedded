package org.avm.business.hmi.web.avm.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.avm.business.core.Avm;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Course;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.util.measurement.State;
import org.stringtree.json.JSONWriter;

public class AvmServlet extends HttpServlet implements ConsumerService {

	private Avm avm;
	private Object lock = new Object();

	public void setAvm(Avm avm) {
		this.avm = avm;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println(req.getPathInfo());
		if (avm != null) {
			String path = req.getPathInfo();
			if (path.equals("/waitstate")) {
				wait4State(resp);
			} else if (path.equals("/listcourses")) {
				listCourses(resp);
			}

		}
	}

	private void listCourses(HttpServletResponse resp) throws IOException {
		Course[] courses = avm.getModel().getServiceAgent().getCourses();
		SimpleCourse[] simpleCourses = new SimpleCourse[courses.length];
		for (int i = 0; i < courses.length; ++i) {
			simpleCourses[i] = new SimpleCourse(courses[i].getIdu(),
					courses[i].getHeureDepart(), courses[i].getDestination(),
					courses[i].isTerminee());
		}
		sendJSON(resp, simpleCourses);
	}

	private void sendJSON(HttpServletResponse resp, Object obj)
			throws IOException {
		JSONWriter writer = new JSONWriter();
		String text = writer.write(obj);
		resp.setContentType("application/json");
		resp.getOutputStream().write(text.getBytes());
	}

	private void wait4State(HttpServletResponse resp) throws IOException {
		try {
			synchronized (lock) {
				lock.wait(10000);
			}
		} catch (InterruptedException e) {

		}
		AvmModel model = avm.getModel();
		sendJSON(resp, model);
	}

	private void wait4JSON() {
		try {
			synchronized (lock) {
				lock.wait(10000);
			}
		} catch (InterruptedException e) {

		}

	}

	private void unblockJSON() {
		synchronized (lock) {
			lock.notify();
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (avm != null) {
			String path = req.getPathInfo();
			if (path.equals("/prisecourse")) {
				String idu = req.getParameter("idu");
				int iidu = getInt(idu);
				if (iidu != -1) {
					avm.priseCourse(iidu);
				}
			}else if (path.equals("/depart")) {
				avm.depart();
			}else if (path.equals("/finservice")){
				avm.finService();
				
			}else if (path.equals("/fincourse")){
				avm.finCourse();
				
			}else if (path.equals("/deviation")){
				avm.sortieItineraire();
			}else if (path.equals("/priseposte")){
				avm.prisePoste(0, 0);
			}
		}
	}

	private int getInt(String val) {
		int ret = -1;
		try {
			ret = Integer.parseInt(val);
		} catch (NumberFormatException nfe) {

		}
		return ret;
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			unblockJSON();
			if (state.getName().equals(UserSessionService.class.getName())) {
				// notifyUserSessionService(state);
			}
			/*
			 * else if (_session != null && _session.getState().getValue() ==
			 * UserSessionService.AUTHENTICATED) { //notifyAvm(state); }
			 */
		}
	}
}
