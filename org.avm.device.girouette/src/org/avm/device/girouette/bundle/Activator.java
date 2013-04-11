package org.avm.device.girouette.bundle;

import java.util.List;

import org.avm.device.girouette.Girouette;
import org.avm.device.girouette.GirouetteDevice;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements AlarmProvider {
	public static final String PID = Girouette.class.getName();

	private ConfigurationAdmin _cm;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private GirouetteDevice _peer;

	private ProducerImpl _producer;

	public Activator() {
		super();
		_peer = new GirouetteDevice();
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
	private void startService() {
		_peer.setContext(_context);
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
		_peer.setContext(null);
	}

	
	public void setGirouette(org.avm.device.girouette.Girouette girouette) {
		_peer.setGirouette(girouette);
	}

	public void unsetGirouette(org.avm.device.girouette.Girouette girouette) {
		_peer.unsetGirouette(girouette);
	}
	
	public List getAlarm() {
		return _peer.getAlarm();
	}

	public String getProducerPID() {
		return _peer.getProducerPID();
	}

}
