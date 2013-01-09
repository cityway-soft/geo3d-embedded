package org.avm.elementary.geofencing.bundle;

import java.util.Set;

import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.geofencing.GeoFencing;
import org.avm.elementary.geofencing.impl.GeoFencingImpl;
import org.avm.elementary.geofencing.impl.GeoFencingManager;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements GeoFencing {
	private static Activator _plugin;
	private ConfigImpl _config;
	private GeoFencingManager _manager;
	private CommandGroupImpl _commands;
	private GeoFencingImpl _peer;
	private ConsumerImpl _consumer;
	private ProducerImpl _producer;

	public Activator() {
		_plugin = this;
		_peer = new GeoFencingImpl();
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
		ConfigurationAdmin cm = (ConfigurationAdmin) _context
				.locateService("cm");
		try {
			_config = new ConfigImpl(_context, cm);
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

	// manager
	private void initializeManager() {
		try {
			_manager = new GeoFencingManager(_context.getBundleContext(),
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

	public Set getZone() {
		return _peer.getZone();
	}
}
