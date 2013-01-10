package org.avm.device.linux.wifi.bundle;

import java.util.Properties;

import org.avm.device.linux.wifi.WifiImpl;
import org.avm.device.wifi.Wifi;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Wifi {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private WifiImpl _peer;

	private AbstractProducer _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
		_peer = new WifiImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeProducer();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
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

	public void connect() {
		_peer.connect();
	}

	public void disconnect() {
		_peer.disconnect();
	}

	public Properties getProperties() {
		return _peer.getProperties();
	}

	public boolean isConnected() {
		return _peer.isConnected();
	}
}
