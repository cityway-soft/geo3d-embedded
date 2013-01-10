package org.avm.business.tad.bundle;

import org.avm.business.tad.Mission;
import org.avm.business.tad.Service;
import org.avm.business.tad.TAD;
import org.avm.business.tad.TADImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements TAD {

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private TAD _peer;

	private ProducerImpl _producer;
	
	private static AbstractActivator _plugin;


	public Activator() {
		_plugin = this;
		_peer = new TADImpl();
	}
	
	public static AbstractActivator getDefault() {
		return _plugin;
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
		_log.debug("startService");
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}
	
	public void add(Mission mission) {
		_peer.add(mission);
	}

	public Service getService() {
		return _peer.getService();
	}

	public void remove(Long id) {
		_peer.remove(id);	
	}
}
