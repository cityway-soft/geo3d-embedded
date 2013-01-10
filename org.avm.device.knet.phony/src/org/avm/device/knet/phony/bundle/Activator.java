package org.avm.device.knet.phony.bundle;

import java.util.Enumeration;

import org.avm.device.knet.KnetAgentFactory;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.phony.PhoneException;
import org.avm.device.knet.phony.PhonyImpl;
import org.avm.device.phony.Phony;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Phony {

	static final String PID = Phony.class.getName();

	private ConfigurationAdmin _cm;

	private PhonyImpl _peer;

	private ProducerImpl _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private KnetAgentFactory _agentFactory;

	public Activator() {
		_peer = new PhonyImpl();
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeProducer();
		initializeCommandGroup();
		initializeAgentKnet();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeAgentKnet();
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

	private void initializeAgentKnet() {
		_log.debug("\tinitializeAgentKnet()");
		_agentFactory = (KnetAgentFactory) _context.locateService("knet");
		if (_peer instanceof KnetDevice) {
			((KnetDevice) _peer).setAgent(_agentFactory);
		}

	}

	private void disposeAgentKnet() {
		_log.debug("\tdisposeAgentKnet()");
		if (_peer instanceof KnetDevice) {
			((KnetDevice) _peer).unsetAgent();
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

	public void answer() {
		_peer.answer();
	}

	public void call(String name) throws PhoneException {
		_peer.call(name);
	}

	public void dial(String number) {
		_peer.dial(number);
	}

	public void hangup() {
		_peer.hangup();
	}

	public Enumeration getContactList() {
		return _peer.getContactList();
	}

	public String getPhoneNumber(String name) {
		return _peer.getPhoneNumber(name);
	}

	public void dialListenMode(String number) {
		_peer.dialListenMode(number);
	}

	public int getDefaultSoundVolume() {
		return _peer.getDefaultSoundVolume();
	}

	public int getSignalQuality() {
		return _peer.getSignalQuality();
	}

	public void setRingSound(int num) throws Exception {
		_peer.setRingSound(num);
	}

	public void setSoundVolume(int val) {
		_peer.setSoundVolume(val);
	}

	public void testRingSound() {
		_peer.testRingSound();
	}

}
