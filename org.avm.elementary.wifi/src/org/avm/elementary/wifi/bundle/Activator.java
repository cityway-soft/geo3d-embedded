package org.avm.elementary.wifi.bundle;

import org.avm.device.wifi.Wifi;
import org.avm.device.wifi.WifiInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.wifi.WifiManager;
import org.avm.elementary.wifi.WifiManagerImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements WifiManager {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private WifiManagerImpl _peer;

	private AbstractProducer _producer;

	private ConsumerImpl _consumer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private Wifi _wifi;

	public Activator() {
		_plugin = this;
		_peer = new WifiManagerImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeProducer();
		initializeCommandGroup();
		initializeConsumer();
		initializeWifi();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeWifi();
		disposeConsumer();
		disposeCommandGroup();
		disposeProducer();
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

	// wifi
	private void initializeWifi() {
		_wifi = (Wifi) _context.locateService("wifi");
		if (_peer instanceof WifiInjector) {
			((WifiInjector) _peer).setWifi(_wifi);
		}
	}

	private void disposeWifi() {
		if (_peer instanceof WifiInjector) {
			((WifiInjector) _peer).setWifi(null);
		}
	}

}
