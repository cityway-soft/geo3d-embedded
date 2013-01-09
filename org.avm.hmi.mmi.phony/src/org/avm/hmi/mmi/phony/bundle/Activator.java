package org.avm.hmi.mmi.phony.bundle;

import org.avm.business.ecall.EcallService;
import org.avm.device.phony.Phony;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.variable.Variable;
import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.phony.MmiPhony;
import org.avm.hmi.mmi.phony.PhonyControler;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements MmiPhony {

	static final String PID = MmiPhony.class.getName();

	// private ComponentContext _context;

	private MmiPhony _peer;

	private ConsumerImpl _consumer;

	public Activator() {
		// _log.setPriority(Priority.DEBUG);
		_peer = new PhonyControler();
	}

	protected void start(ComponentContext context) {
		// _context = context;
		initializeBaseIhm();
		initializeConsumer();
		initializePhony();
		initializeEcall();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeEcall();
		disposePhony();
		disposeConsumer();
		disposeBaseIhm();
	}

	// Base
	private void initializeBaseIhm() {
		_log.debug("initialize base ihm");
		AVMDisplay base = null;
		try {
			base = (AVMDisplay) _context.locateService("base");
			setBase(base);
		} catch (Exception e) {
			_log.error("initialize error", e);
		}
	}

	private void disposeBaseIhm() {
		setBase(null);
	}

	// consumer
	private void initializeConsumer() {
		_log.debug("initializeConsumer");
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

	private void initializePhony() {
		_log.debug("\tinitializePhony()");
		Phony phony = (Phony) _context.locateService("phony");
		if (phony == null)
			_log.warn("phony n'a pu etre trouve ...");
		setPhony(phony);
	}

	private void disposePhony() {
		_log.debug("\tdisposePhony()");
		setPhony(null);
	}

	private void initializeEcall() {
		_log.debug("initializeEcall");
		EcallService ecall = (EcallService) _context.locateService("ecall");
		setEcall(ecall);
	}

	private void disposeEcall() {
		setEcall(null);
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

	public void notify(Object o) {
		_peer.notify(o);
	}

	public void start() {
		_peer.start();
	}

	public void stop() {
		_peer.stop();
	}

	public void setBase(AVMDisplay base) {
		_log.debug("setBase " + base);
		_peer.setBase(base);
	}

	public void unsetBase(AVMDisplay base) {
		_log.debug("unset Base");
		_peer.setBase(null);
	}

	public void setPhony(Phony phony) {
		_peer.setPhony(phony);
	}

	public void setEcall(EcallService ecall) {
		_peer.setEcall(ecall);
	}

	public void setBeeper(Variable var) {
		// _log.debug("setBeeper(" + var + ")");
		_peer.setBeeper(var);
	}

	public void unsetBeeper(Variable var) {
		// _log.debug("unsetBeeper(" + var + ")");
		_peer.setBeeper(null);
	}

}
