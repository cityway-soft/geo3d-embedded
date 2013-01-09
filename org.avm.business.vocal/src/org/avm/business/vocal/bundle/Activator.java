package org.avm.business.vocal.bundle;

import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.vocal.Vocal;
import org.avm.business.vocal.VocalImpl;
import org.avm.business.vocal.VocalManager;
import org.avm.device.player.Player;
import org.avm.device.player.PlayerInjector;
import org.avm.device.sound.Sound;
import org.avm.device.sound.SoundInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.variable.Variable;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Vocal, AvmInjector, PlayerInjector, SoundInjector {
	private static Activator _plugin;
	private ConfigurationAdmin _cm;
	private VocalImpl _peer;
	private ConfigImpl _config;
	private ConsumerImpl _consumer;
	private CommandGroupImpl _commands;
	private VocalManager _manager;

	public Activator() {
		super();
		_plugin = this;
		_peer = new VocalImpl();
		// _log.setPriority(Priority.DEBUG);
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeConsumer();
		initializeCommandGroup();
		initializeManager();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeManager();
		disposeCommandGroup();
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

	// manager
	private void initializeManager() {
		try {
			_manager = new VocalManager(_context.getBundleContext(), _config);
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
			_consumer = new ConsumerImpl(_context, (ConsumerService) _peer);
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

	public void unsetAudioConducteur(Variable variable) {
		_log.debug("unsetAudioConducteur " + variable);
		_peer.unsetAudioConducteur(variable);
	}

	public void setAudioConducteur(Variable variable) {
		_log.debug("setAudioConducteur " + variable);
		_peer.setAudioConducteur(variable);
	}

	public void unsetIOAudioVoyageurInterieur(Variable variable) {
		_log.debug("unsetIOAudioVoyageurInterieur " + variable);
		_peer.unsetIOAudioVoyageurInterieur(variable);
	}

	public void setIOAudioVoyageurInterieur(Variable variable) {
		_log.debug("setIOAudioVoyageurInterieur " + variable);
		_peer.setIOAudioVoyageurInterieur(variable);
	}
	
	public void unsetIOAudioVoyageurExterieur(Variable variable) {
		_log.debug("unsetIOAudioVoyageurExterieur " + variable);
		_peer.unsetIOAudioVoyageurExterieur(variable);
	}

	public void setIOAudioVoyageurExterieur(Variable variable) {
		_log.debug("setIOAudioVoyageurExterieur " + variable);
		_peer.setIOAudioVoyageurExterieur(variable);
	}
	
	public void setAvm(org.avm.business.core.Avm avm) {
		_log.debug("setAvm " + avm);
		_peer.setAvm(avm);
	}

	public void unsetAvm(Avm avm) {
		_log.debug("unset Avm");
		_peer.setAvm(null);
	}

	public void setPlayer(Player player) {
		_log.debug("set Player");
		_peer.setPlayer(player);
	}

	public void unsetPlayer(Player player) {
		_log.debug("unset Player");
		_peer.unsetPlayer(player);
	}

	public void setSound(Sound sound) {
		_log.debug("unset Sound");
		_peer.setSound(sound);
	}

	public void unsetSound(Sound sound) {
		_log.debug("unset Sound");
		_peer.unsetSound(sound);
	}
}
