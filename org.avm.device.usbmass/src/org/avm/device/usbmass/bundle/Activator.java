package org.avm.device.usbmass.bundle;

import org.apache.log4j.Logger;
import org.avm.device.usbmass.UsbMass;
import org.avm.device.usbmass.UsbMassImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.AbstractProducer;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements UsbMass {
	private static AbstractActivator _plugin;

	private ConfigurationAdmin _cm;

	private UsbMassImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private AbstractProducer _producer;

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		_peer = new UsbMassImpl();
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		initializeProducer();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeProducer();
		disposeCommandGroup();
		disposeConfiguration();
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

	public boolean isAvailable() {
		return _peer.isAvailable();
	}

	public static AbstractActivator getDefault() {
		return _plugin;
	}
}
