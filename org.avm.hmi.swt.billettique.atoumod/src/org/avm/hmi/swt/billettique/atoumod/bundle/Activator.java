package org.avm.hmi.swt.billettique.atoumod.bundle;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.business.billettique.atoumod.Billettique;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.hmi.swt.billettique.atoumod.BillettiqueIhm;
import org.avm.hmi.swt.billettique.atoumod.BillettiqueIhmImpl;
import org.avm.hmi.swt.billettique.atoumod.BillettiqueImpl;
import org.avm.hmi.swt.desktop.Desktop;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements BillettiqueIhm {

	static final String PID = BillettiqueIhmImpl.class.getName();

	private static AbstractActivator _plugin;

	private BillettiqueImpl _peer;

	private ConsumerImpl _consumer;

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_peer = new BillettiqueImpl();
	}

	protected void start(ComponentContext context) {
		initializeContext();
		initializeBillettiqueService();
		initializeDesktop();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeDesktop();
		disposeBillettiqueService();
		disposeContext();

	}

	// alarm service
	private void initializeBillettiqueService() {
		Billettique girouette = (Billettique) _context
				.locateService("billettique");
		_peer.setBillettique(girouette);
	}

	private void disposeBillettiqueService() {
		_peer.setBillettique(null);
	}

	private void initializeContext() {
		_peer.setContext(_context);
	}

	private void disposeContext() {
		_peer.setContext(null);
	}

	// Desktop
	private void initializeDesktop() {
		Desktop base = (Desktop) _context.locateService("desktop");
		_peer.setDesktop(base);
	}

	private void disposeDesktop() {
		_peer.setDesktop(null);
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

	public void start() {
		_peer.start();
	}

	public void stop() {
		_peer.stop();
	}

	public String getProducerPID() {
		return _peer.getProducerPID();
	}

}
