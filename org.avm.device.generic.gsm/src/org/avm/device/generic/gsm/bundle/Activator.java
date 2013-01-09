package org.avm.device.generic.gsm.bundle;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.generic.gsm.GsmImpl;
import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmException;
import org.avm.device.gsm.GsmRequest;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Gsm {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private GsmImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private ProducerImpl _producer;

	public Activator() {
		_plugin = this;		
		_peer = new GsmImpl();		
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
		disposeProducer();
		disposeCommandGroup();
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

	public void send(GsmRequest command) throws GsmException,
			InterruptedException, IOException {
		_peer.send(command);
	}

	
	public int getSignalQuality() {
		return _peer.getSignalQuality();
	}

	
	public boolean isGprsAttached() {
		return _peer.isGprsAttached();
	}

	
	public boolean isGsmAttached() {
		return _peer.isGsmAttached();
	}

}
