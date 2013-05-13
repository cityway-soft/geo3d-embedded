package org.avm.hmi.swt.girouette.bundle;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.business.girouette.Girouette;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.girouette.GirouetteIhm;
import org.avm.hmi.swt.girouette.GirouetteIhmImpl;
import org.avm.hmi.swt.girouette.GirouetteImpl;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements GirouetteIhm {

	static final String PID = GirouetteIhmImpl.class.getName();

	private static AbstractActivator _plugin;

	private GirouetteImpl _peer;

	private ConsumerImpl _consumer;

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_peer = new GirouetteImpl();
	}

	protected void start(ComponentContext context) {
		initializeContext();
		initializeGirouetteService();
		initializeDesktop();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeDesktop();
		disposeGirouetteService();
		disposeContext();
		
	}

	// alarm service
	private void initializeGirouetteService() {
		Girouette girouette = (Girouette) _context
				.locateService("girouette");
		_peer.setGirouette(girouette);
	}

	private void disposeGirouetteService() {
		_peer.setGirouette(null);
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
