package org.angolight.halfcycle.bundle;

import org.angolight.halfcycle.HalfCycleService;
import org.angolight.halfcycle.impl.HalfCycleDeployer;
import org.angolight.halfcycle.impl.HalfCycleServiceImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements HalfCycleService {
	private ConfigurationAdmin _cm;

	private HalfCycleServiceImpl _peer;

	private ConsumerImpl _consumer;

	private ProducerImpl _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private HalfCycleDeployer _manager;

	private static Activator _plugin;

	public Activator() {
		_plugin = this;
		_peer = new HalfCycleServiceImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeConsumer();
		initializeProducer();
		initializeCommandGroup();
		initializeManager();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeManager();
		disposeCommandGroup();
		disposeProducer();
		disposeConsumer();
		disposeConfiguration();
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

	// manager
	private void initializeManager() {
		try {
			_manager = new HalfCycleDeployer(_context.getBundleContext(),
					_config);
			_manager.start();
		} catch (Exception e) {
			_log.error(e.getMessage());
		}
	}

	private void disposeManager() {
		if (_manager != null)
			_manager.stop();
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

}
