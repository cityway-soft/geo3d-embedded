package org.avm.hmi.swt.authentification.bundle;

import org.apache.log4j.Logger;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.authentification.Authentification;
import org.avm.hmi.swt.authentification.AuthentificationImpl;
import org.avm.hmi.swt.desktop.Desktop;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Authentification, UserSessionServiceInjector {

	private static AbstractActivator _plugin;

	private AuthentificationImpl _peer;

	private ConsumerImpl _consumer;

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		_peer = new AuthentificationImpl();
	}

	protected void start(ComponentContext context) {
		try {
			initializeDesktop();
			startService();
			initializeConsumer();
		} catch (Throwable t) {
			_log.error("ACTIVATION ERROR ", t);
		}
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeDesktop();
	}

	// consumer
	private void initializeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer = new ConsumerImpl(_context, _peer);
			_consumer.start();
		}
	}

	private void disposeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer.stop();
		}
	}

	// Base
	private void initializeDesktop() {
		Desktop base = (Desktop) _context.locateService("desktop");
		_peer.setBase(base);
	}

	private void disposeDesktop() {
		_peer.setBase(null);
	}

	// service
	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
	}

	private void startService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public void setUserSessionService(UserSessionService uss) {
		_log.debug("setUserSessionService = " + uss);
		_peer.setUserSessionService(uss);
	}

	public void unsetUserSessionService(UserSessionService uss) {
		_log.debug("unsetUserSessionService");
		_peer.unsetUserSessionService(null);
	}

}
