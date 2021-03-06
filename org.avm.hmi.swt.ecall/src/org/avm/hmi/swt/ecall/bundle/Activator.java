package org.avm.hmi.swt.ecall.bundle;

import org.apache.log4j.Logger;
import org.avm.business.ecall.EcallService;
import org.avm.business.ecall.EcallServiceInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.ecall.Ecall;
import org.avm.hmi.swt.ecall.EcallImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Ecall, EcallServiceInjector {

	private ConfigurationAdmin _cm;

	private EcallImpl _peer;

	private ConsumerImpl _consumer;
	
	private static AbstractActivator _plugin;

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		_peer = new EcallImpl();
	}

	protected void start(ComponentContext context) {
		try {
			initializeConsumer();
			initializeBaseIhm();
			initializeEcallService();
			startService();
		} catch (Throwable t) {
			_log.error("ACTIVATION ERROR ", t);
		}
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeEcallService();
		disposeBaseIhm();
		disposeConsumer();
	}

	// Base
	private void initializeBaseIhm() {
		_log.debug("initialize desktop ihm");
		Desktop base = (Desktop) _context.locateService("desktop");
		_peer.setBase(base);
	}

	private void disposeBaseIhm() {
		_peer.setBase(null);
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

	// ecall service
	private void initializeEcallService() {
		_log.debug("initializeEcallService");
		EcallService service = (EcallService) _context.locateService("ecall");
		_peer.setEcallService(service);
	}

	private void disposeEcallService() {
		_peer.setEcallService(null);
	}

	public void start() {
		_peer.start();
	}

	public void stop() {
		_peer.stop();
	}

	public void etatAttentePriseEnCharge() {
		_peer.etatAttentePriseEnCharge();
	}

	public void etatEcouteDiscrete() {
		_peer.etatEcouteDiscrete();
	}

	public void etatInitial() {
		_peer.etatInitial();
	}

	public void setEcallService(EcallService ecallService) {
		_log.debug("setEcallService(" + ecallService + ")");
		_peer.setEcallService(ecallService);
	}

	public void unsetEcallService(EcallService service) {
		_log.debug("unsetEcallService");
		_peer.setEcallService(null);
	}

	public static AbstractActivator getDefault() {
		return _plugin;
	}

}
