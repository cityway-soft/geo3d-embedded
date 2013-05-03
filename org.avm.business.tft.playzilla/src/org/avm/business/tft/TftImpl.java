package org.avm.business.tft;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.core.event.Point;
import org.avm.business.messages.Messages;
import org.avm.business.messages.MessagesInjector;
import org.avm.business.tft.bundle.Activator;
import org.avm.business.tft.model.Util;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.osgi.util.measurement.State;

public class TftImpl implements Tft, ConsumerService, ManageableService,
		ConfigurableService, AvmInjector, MessagesInjector {

	private static final long TIMEOUT = 60 * 1000;
	public static final SimpleDateFormat HDF = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private Logger _log = Activator.getDefault().getLogger();
	private TftConfig _config;

	public Config getConfig() {
		return _config;
	}

	private Avm _avm;
	private Object _lock = new Object();
	private long _count;

	private Messages _messages;

	public TftImpl() {
	}

	public void configure(Config config) {
		_config = (TftConfig) config;
	}

	public void setAvm(Avm avm) {
		_avm = avm;
	}

	public void unsetAvm(Avm avm) {
		_avm = null;
	}

	public void setMessages(Messages messages) {
		_messages = messages;
	}

	public void unsetMessages(Messages messages) {
		_messages = null;
	}

	public void start() {
	}

	public void stop() {
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (_log.isDebugEnabled()) {
				_log.debug("notify state:" + o);
			}
			if ((state.getName().equals(Messages.class.getName()) && state
					.getValue() == Messages.VOYAGEUR)
					|| state.getName().equals(Avm.class.getName())) {
				refresh();
			}
		}
	}

	public void refresh() {
		synchronized (_lock) {
			_count++;
			_lock.notifyAll();
		}
	}

	private void trace(HttpServletRequest request, String message) {
		StringBuffer sb = new StringBuffer();
		sb.append("[*******] ");
		sb.append("[" + request.getRemoteAddr() + "] ");
		sb.append(message);
		_log.debug(sb.toString());
	}

	public void process(HttpServletRequest request, HttpServletResponse response)
			throws Throwable {
		try {
			_process(request, response);
		} catch (Throwable t) {
			_log.error("Error doGet :", t);
			throw t;
		}
	}


	

	public void _process(HttpServletRequest request,
			HttpServletResponse response) {
		_log.debug(request.getRemoteAddr() + " query "
				+ request.getQueryString());

		boolean init = false;
		String value = request.getParameter("init");
		if (value != null) {
			init = Boolean.valueOf(value).booleanValue();
		}

		HttpSession session = request.getSession(true);
		if (!session.isNew() && !init) {
			// long count = ((Long) session.getAttribute("count")).longValue();
			// ==> java.lang.UnsupportedOperationException: HttpService only
			// supports servlet 2.1 specification.
			long count = ((Long) session.getValue("count")).longValue();

			trace(request, "count " + count + "/" + _count);
			if (count == _count) {
				synchronized (_lock) {
					try {
						trace(request,
								" wait " + TIMEOUT + " thread "
										+ Thread.currentThread());
						_lock.wait(TIMEOUT);
					} catch (InterruptedException e) {
					}
				}
			}
		}

		_log.debug("avm=" + _avm);

		if (_avm != null) {
			_log.debug("building response...");
			// session.setAttribute("count", new Long(_count));
			// ==> java.lang.UnsupportedOperationException: HttpService only
			// supports servlet 2.1 specification.
			session.putValue("count", new Long(_count));

			session.setMaxInactiveInterval(120);
			response.setContentType("text/plain");
			try {
				PrintWriter writer = response.getWriter();
				String ligneIdu = null;
				if (_avm.getModel().getCourse() != null) {
					ligneIdu = Integer.toString(_avm.getModel().getCourse().getLigneIdu());
				}

				Collection messages = _messages.getMessages(Messages.VOYAGEUR,
						ligneIdu);
				if (_log.isDebugEnabled()) {
					Iterator iter = messages.iterator();
					while (iter.hasNext()) {
						_log.debug(iter.next());
					}
				}

				String text = Util.toJSONString(_avm.getModel(), messages);
				if (text != null) {
					writer.println(text);
				}

				writer.close();
				trace(request, "thread " + Thread.currentThread() + "response "
						+ text);
			} catch (IOException e) {
				_log.error(e);
				_log.error("model:" + _avm.getModel());
				_log.error("json:" + Util.toJSONString(_avm.getModel(), null));
			}
		}

	}

	class OrderByPriorityAndEndDate implements Comparator {

		public int compare(Object object1, Object object2) {
			Properties p1 = (Properties) object1;
			Properties p2 = (Properties) object2;

			Date date1 = null;
			Date date2 = null;
			int prio1 = 0;
			int prio2 = 0;

			try {
				prio1 = Integer.parseInt((String) p1
						.get(org.avm.business.messages.Messages.PRIORITE));
			} catch (NumberFormatException e) {
				_log.error(e);
			}

			try {
				prio2 = Integer.parseInt((String) p2
						.get(org.avm.business.messages.Messages.PRIORITE));
			} catch (NumberFormatException e) {
				_log.error(e);
			}

			if (prio1 < prio2) {
				_log.debug("PRIO:" + object1 + "<" + object2);

				return 1;
			} else if (prio1 > prio2) {
				_log.debug("PRIO:" + object1 + ">" + object2);

				return -1;
			}

			try {
				String d = (String) p1
						.get(org.avm.business.messages.Messages.FIN);
				date1 = HDF.parse(d);
			} catch (ParseException e) {
				_log.error(e);
			}
			try {
				String d = (String) p2
						.get(org.avm.business.messages.Messages.FIN);
				date2 = HDF.parse(d);
			} catch (ParseException e) {
				_log.error(e);
			}

			if (date1 == null && date2 == null) {
				_log.debug("DATES NULLES:" + object1 + "=" + object2);
				return 0;
			}

			if (date1 == null || date1.before(date2)) {
				_log.debug("DATE:" + object1 + "<" + object2);
				return -1;
			} else if (date2 == null || date1.after(date2)) {
				_log.debug("DATE:" + object1 + ">" + object2);
				return 1;
			} else {
				_log.debug("DATE:" + object1 + "=" + object2);
				return 0;
			}

		}
	}

}