package org.avm.hmi.swt.alarm.bundle;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.hmi.swt.alarm.AlarmIhm;
import org.avm.hmi.swt.alarm.AlarmImpl;
import org.avm.hmi.swt.desktop.Desktop;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements AlarmIhm, AlarmProvider{

	static final String PID = AlarmIhm.class.getName();
	private AlarmImpl _peer;

	private ConsumerImpl _consumer;

	private org.avm.hmi.swt.alarm.bundle.ProducerImpl _producer;



	private static AbstractActivator _plugin;

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_peer = new AlarmImpl();
	}

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeAlarmService();
//		initializeContext();
		initializeDesktop();
		initializeProducer();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeProducer();
		disposeDesktop();
//		disposeContext();
		disposeAlarmService();
	}
	
	// alarm service
	private void initializeAlarmService() {
		AlarmService alarmeService = (AlarmService) _context.locateService("alarm");
		_peer.setAlarmService(alarmeService);
	}

	private void disposeAlarmService() {
		_peer.setAlarmService(null);
	}


	
	// Desktop
	private void initializeDesktop() {
		Desktop base = (Desktop) _context.locateService("desktop");
		_peer.setBase(base);
	}

	private void disposeDesktop() {
		_peer.setBase(null);
	}

	// producer
	private void initializeProducer() {
		if (_peer instanceof ProducerService) {
			_producer = new ProducerImpl(_context);
			_producer.start();
			((ProducerService) _peer).setProducer(_producer);
		}
	}

	private void disposeProducer() {
		if (_peer instanceof ProducerService) {
			((ProducerService) _peer).setProducer(null);
			_producer.stop();
		}
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

	public List getAlarm() {
		return _peer.getAlarm();
	}

	public String getProducerPID() {
		return _peer.getProducerPID();
	}

//	public void setUserSessionService(UserSessionService service) {
//		_peer.setUserSessionService(service);
//	}
//
//	public void unsetUserSessionService(UserSessionService service) {
//		_peer.unsetUserSessionService(service);
//	}

}
