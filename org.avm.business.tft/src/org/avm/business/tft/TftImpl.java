package org.avm.business.tft;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
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
			_log.info("notify state:" + o);
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
				String ligneIdu = "";
				if (_avm.getModel().getCourse() != null) {
					ligneIdu = Integer.toString(_avm.getModel().getCourse()
							.getLigneIdu());
				}
				Collection messages = _messages.getMessages(Messages.VOYAGEUR,
						ligneIdu);
				String[] msg = new String[messages.size() * 2];
				Iterator iter = messages.iterator();
				int i = 0;
				String from = System.getProperty("from.charset", "UTF-8");
				String to = System.getProperty("to.charset", "iso-8859-1");
				while (iter.hasNext()) {
					Properties props = (Properties) iter.next();

					String temp = props
							.getProperty(org.avm.business.messages.Messages.MESSAGE);

					_log.debug("from=" + from + ", to=" + to);
					if (from != null && to != null) {
						_log.debug("Avant conversion " + temp);
						try {
							byte[] m = new String(temp.getBytes(), from)
									.getBytes(to);
							temp = new String(m);
							_log.debug("Après conversion " + temp);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

					} else {
						_log.debug("Aucune conversion " + temp);
					}

					msg[i] = temp;
					msg[i + 1] = props.getProperty(Messages.PRIORITE);

					i += 2;
				}

				String text = Util.toJSONString(_avm.getModel(), msg);
				writer.println(text);

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

}