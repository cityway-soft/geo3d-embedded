package org.avm.device.linux.sound.bundle;

import org.avm.device.linux.sound.SoundImpl;
import org.avm.device.sound.Sound;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Sound {

	private static Activator _plugin;
	private ConfigurationAdmin _cm;
	private Sound _peer;
	private CommandGroupImpl _commands;
	private ConfigImpl _config;

	public Activator() {
		_plugin = this;
		_peer = new SoundImpl();
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

	public void configure(String name) throws Exception {
		_peer.configure(name);
	}

	public void setMasterVolume(short volume) {
		_peer.setMasterVolume(volume);
	}
}
