package org.avm.device.linux.player.mp3.bundle;

import org.avm.device.linux.player.mp3.Mp3Player;
import org.avm.device.player.Player;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Player {

	private static Activator _plugin;
	private ConfigurationAdmin _cm;
	private Mp3Player _peer;
	private CommandGroupImpl _commands;
	private ConfigImpl _config;

	public Activator() {
		_plugin = this;
		_peer = new Mp3Player();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
	}

	protected void stop(ComponentContext context) {
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

	public void close() {
		_peer.close();
	}

	public void open(String name) {
		_peer.open(name);
	}

	public void pause() {
		_peer.pause();
	}

	public void play() {
		_peer.play();
	}

	public void resume() {
		_peer.resume();
	}

	public void stop() {
		_peer.stop();
	}
}
