package org.avm.hmi.mmi.authentification.bundle;

import org.apache.log4j.Priority;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.session.UserSession;
import org.avm.elementary.useradmin.session.UserSessionListener;
import org.avm.elementary.useradmin.session.UserSessionManager;
import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.authentification.Authentification;
import org.avm.hmi.mmi.authentification.AuthentificationControler;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Authentification,
		UserSessionListener {

	static final String PID = Authentification.class.getName();

	private ComponentContext _context;

	private AuthentificationControler _peer;

	public Activator() {
		// _log.setPriority(Priority.DEBUG);
		_log.debug("Activator");
		_peer = new AuthentificationControler();
	}

	public void start(ComponentContext context) {
		try {
			_context = context;
			initializeUserSession();
			initializeBaseIhm();
			startService();
		} catch (Throwable t) {
			_log.error("Start bundle", t);
		}
	}

	public void stop(ComponentContext context) {
		stopService();
		disposeBaseIhm();
		disposeUserSession();
	}

	// Base
	private void initializeBaseIhm() {
		_log.debug("initialize base ihm");
		AVMDisplay base = (AVMDisplay) _context.locateService("base");
		_peer.setBase(base);
	}

	private void disposeBaseIhm() {
		_peer.setBase(null);
	}

	// Base
	private void initializeUserSession() {
		_log.debug("initialize UserSessionManager");
		UserSessionManager ua = (UserSessionManager) _context
				.locateService("usersession");
		_peer.setUserSession(ua);
	}

	private void disposeUserSession() {
		_peer.setUserSession(null);
	}

	// service
	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
	}

	private void startService() {
		_log.debug("startService");
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public void login(UserSession session) {
		_peer.loggedOn();
	}

	public void logout(UserSession session) {
		_peer.loggedOut();
	}
}
