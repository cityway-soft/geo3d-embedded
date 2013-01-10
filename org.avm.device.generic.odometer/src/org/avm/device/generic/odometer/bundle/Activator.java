package org.avm.device.generic.odometer.bundle;

import org.avm.device.generic.odometer.OdometerImpl;
import org.avm.device.gps.Gps;
import org.avm.device.gps.GpsInjector;
import org.avm.device.odometer.Odometer;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.common.BundleThreadFactory;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.common.ThreadFactoryInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Odometer {

	static final String PID = Odometer.class.getName();

	private ConfigurationAdmin _cm;

	private OdometerImpl _peer;

	private AbstractProducer _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private ConsumerImpl _consumer;

	public Activator() {
		_peer = new OdometerImpl();
	}

	protected void start(ComponentContext context) {
		initializeThreadFactory();
		initializeConfiguration();
		initializeProducer();
		initializeConsumer();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeCommandGroup();
		disposeConsumer();
		disposeProducer();
		disposeConfiguration();
		disposeThreadFactory();
	}

	// thread factory
	private void initializeThreadFactory() {
		if (_peer instanceof ThreadFactoryInjector) {
			_peer.setThreadFactory(new BundleThreadFactory(_context
					.getBundleContext()));
		}
	}

	private void disposeThreadFactory() {
		if (_peer instanceof ThreadFactoryInjector) {
			_peer.setThreadFactory(null);
		}
	}

	// config
	private void initializeConfiguration() {

		_cm = (ConfigurationAdmin) _context.locateService("cm");
		try {
			_config = new ConfigImpl(_context, _cm);
			_config.start();
			if (_peer instanceof ConfigurableService) {
				((ConfigurableService) _peer).configure(_config);

			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

	}

	private void disposeConfiguration() {
		_config.stop();
		if (_peer instanceof ConfigurableService) {
			((ConfigurableService) _peer).configure(null);
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

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
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

	public int getCounterValue() {
		return _peer.getCounterValue();
	}

	public double getCounterFactor() {
		return _peer.getCounterFactor();
	}

}
