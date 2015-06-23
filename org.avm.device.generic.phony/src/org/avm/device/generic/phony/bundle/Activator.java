package org.avm.device.generic.phony.bundle;

import org.avm.device.generic.phony.impl.PhonyImpl;
import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmInjector;
import org.avm.device.phony.Phony;
import org.avm.device.sound.Sound;
import org.avm.device.sound.SoundInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.variable.Variable;
import org.avm.elementary.variable.VariableInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Phony,
		VariableInjector, GsmInjector, SoundInjector, JDBInjector {
	private static Activator _plugin;
	private ConfigurationAdmin _cm;
	private PhonyImpl _peer;
	private ProducerImpl _producer;
	private ConfigImpl _config;
	private CommandGroupImpl _commands;
	private ConsumerImpl _consumer;

	public Activator() {
		_plugin = this;
		_peer = new PhonyImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		try {
			initializeConfiguration();
			initializeCommandGroup();
			initializeProducer();
			startService();
			initializeConsumer();
		} catch (Throwable t) {
			_log.error("ACTIVATION ERROR ", t);
		}
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
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

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
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

	public void answer() throws Exception {
		_peer.answer();
	}

	public void dial(String number) throws Exception {
		_peer.dial(number);
	}

	public void hangup() throws Exception {
		_peer.hangup();
	}

	public void dialListenMode(String phone) throws Exception {
		_peer.dialListenMode(phone);
	}

	public void setVolume(int val) {
		_peer.setVolume(val);
	}

	public int getDefaultSoundVolume() {
		return _peer.getDefaultSoundVolume();
	}

	public void setVariable(Variable var) {
		_peer.setVariable(var);
	}

	public void unsetVariable(Variable var) {
		_peer.unsetVariable(var);
	}

	public void setGsm(Gsm gsm) {
		_peer.setGsm(gsm);
	}

	public void unsetGsm(Gsm gsm) {
		_peer.unsetGsm(null);
	}

	public void setSound(Sound sound) {
		_peer.setSound(sound);
	}

	public void unsetSound(Sound sound) {
		_peer.unsetSound(sound);
	}

	public void setJdb(JDB jdb) {
		_peer.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_peer.unsetJdb(jdb);
	}

	public void setRingVolume(int volume) {
		_peer.setRingVolume(volume);
		
	}

}
