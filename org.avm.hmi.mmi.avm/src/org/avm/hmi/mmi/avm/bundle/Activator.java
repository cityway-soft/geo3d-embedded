package org.avm.hmi.mmi.avm.bundle;

import org.avm.business.core.Avm;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.session.UserSession;
import org.avm.elementary.useradmin.session.UserSessionListener;
import org.avm.elementary.variable.Variable;
import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.avm.AvmControler;
import org.avm.hmi.mmi.avm.AvmMmi;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements AvmMmi,
		UserSessionListener {

	static final String PID = AvmMmi.class.getName();

	private AvmMmi _peer;

	private ConsumerImpl _consumer;

	public Activator() {
		// _log.setPriority(Priority.DEBUG);
		_peer = new AvmControler();
	}

	protected void start(ComponentContext context) {
		try {
			initializeBaseIhm();
			initializeConsumer();
			initializeAvmCore();
		} catch (RuntimeException re) {
			_log.error("erreur starting avm ihm", re);
		}
	}

	protected void stop(ComponentContext context) {
		try {
			disposeAvmCore();
			disposeConsumer();
			disposeBaseIhm();
		} catch (RuntimeException re) {
			_log.error("erreur stopping avm ihm", re);
		}
	}

	// avmcore
	private void initializeAvmCore() {
		_log.debug("initializeAVM");
		Avm avm = (Avm) _context.locateService("avmcore");
		_log.debug("initialize AVM =" + avm);
		setAvm(avm);
	}

	private void disposeAvmCore() {
		setAvm(null);
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

	// consumer
	private void initializeConsumer() {
		_log.debug("initializeConsumer");
		if (_peer instanceof ConsumerService) {
			_consumer = new ConsumerImpl(_context, (ConsumerService) _peer);
			_consumer.start();
		}
	}

	private void disposeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer.stop();
		}
	}

	// service
	private void stopService() {
		_log.debug("stopService");
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

	public void setAvm(Avm avm) {
		_log.debug("setAvm " + avm);
		_peer.setAvm(avm);
	}

	public void unsetAvm(AvmMmi avm) {
		_log.debug("unset Avm");
		_peer.setAvm(null);
	}

	public void setBase(AVMDisplay base) {
		_log.debug("setBase " + base);
		_peer.setBase(base);
	}

	public void unsetBase(AVMDisplay base) {
		_log.debug("unset Base");
		_peer.setBase(null);
	}

	public void setBeeper(Variable var) {
		_log.debug("setBeeper(" + var + ")");
		_peer.setBeeper(var);
	}

	public void unsetBeeper(Variable var) {
		_log.debug("unsetBeeper(" + var + ")");
		_peer.setBeeper(null);
	}

	public void login(UserSession session) {
		_log.debug("login");
		if (session.hasRole("conducteur")) {
			startService();
		}
		if (session.hasRole("mainteneur")) {
			isMainteneur();
		}
	}

	public void logout(UserSession session) {
		_log.debug("logout");
		stopService();
	}

	public void isMainteneur() {
		_peer.isMainteneur();
	}
}
