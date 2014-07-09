package org.avm.business.hmi.web.avm.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.avm.business.core.Avm;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Course;
import org.avm.business.hmi.web.utils.HmiServlet;
import org.avm.business.hmi.web.utils.LongPollingWaiter;
import org.avm.business.hmi.web.utils.ResponseWaiter;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.osgi.util.measurement.State;
import org.stringtree.json.JSONWriter;

public class AvmServlet extends HmiServlet implements ConsumerService {

	private Avm avm;
	private LongPollingWaiter responseWaiter = new LongPollingWaiter();

	public void setAvm(Avm avm) {
		this.avm = avm;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (avm != null) {
			String path = req.getPathInfo();
			if (path.equals("/waitstate")) {
				wait4State(req, resp);
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

	private void wait4State(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if (!isNoWait(req)) {
			responseWaiter.waitToDoResponse(req);
		}
		AvmModel model = avm.getModel();
		sendJSON(resp, model);
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
			} else if (path.equals("/priseservice")) {
				String idu = req.getParameter("idu");
				int iidu = getInt(idu);
				if (iidu != -1) {
					avm.priseService(iidu);
				}
			} else if (path.equals("/depart")) {
				avm.depart();
			} else if (path.equals("/finservice")) {
				avm.finService();

			} else if (path.equals("/fincourse")) {
				avm.finCourse();

			} else if (path.equals("/deviation")) {
				avm.sortieItineraire();
			} else if (path.equals("/priseposte")) {
				avm.prisePoste(0, 0);
			}
		}
	}

	public void notify(Object o) {
		if (o instanceof State) {
			System.out.println("in notify ");
			responseWaiter.unlockRequest();
		}
	}
}
