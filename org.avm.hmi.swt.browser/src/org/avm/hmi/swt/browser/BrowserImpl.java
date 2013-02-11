package org.avm.hmi.swt.browser;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.util.measurement.State;

public class BrowserImpl implements ManageableService, ConsumerService,
		UserSessionServiceInjector {
	protected static final String NAME = "Navigateur";

	private BrowserIhm _browserIhm;

	private Desktop _desktop;

	private Logger _log;

	private Display _display;

	private UserSessionService _session;

	private boolean _logged=false;

	public BrowserImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public void setBase(Desktop base) {
		_desktop = base;
		if (_desktop != null) {
			_display = _desktop.getDisplay();
		}
	}

	public void start() {
		_log.info("starting...");
		login();
		_log.info("started.");
	}

	public void stop() {
		_log.info("stopping...");
		logout();
		_log.info("stopped.");
	}

	public void open() {
		_display.syncExec(new Runnable() {
			public void run() {
				_browserIhm = new BrowserIhm(_desktop.getMiddlePanel(), SWT.NONE);
				_desktop.addTabItem(NAME, _browserIhm);
			}
		});
	}
	
	public void setUrl(final URL url){
		_display.syncExec(new Runnable() {
			public void run() {
				_browserIhm.setUrl(url);
			}
		});
	}

	public void close() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (_browserIhm != null && _browserIhm.isDisposed() == false) {
					_browserIhm.dispose();
					_desktop.removeTabItem(NAME);
				}
			}
		});
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(UserSessionService.class.getName())) {
				if (state.getValue() == UserSessionService.AUTHENTICATED){
					login();
				} else {
					logout();
				}
			} 
		}
	}

	public void login() {
		if (_session != null && !_logged) {
			open();
			try {
				setUrl(new URL("http://www.google.fr"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_logged = true;
		}
	}

	public void logout() {
		if (_logged){
			close();
			_logged=false;
		}
	}





	public void setUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void unsetUserSessionService(UserSessionService service) {
		_session = service;
	}

}