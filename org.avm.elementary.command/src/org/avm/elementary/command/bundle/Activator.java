package org.avm.elementary.command.bundle;

import java.net.URL;

import org.avm.elementary.command.CommandChain;
import org.avm.elementary.command.CommandChainContext;
import org.avm.elementary.command.impl.CommandChainImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.osgi.framework.Bundle;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements CommandChain {

	private static org.avm.elementary.command.bundle.Activator _plugin;

	private ConfigurationAdmin _cm;

	private CommandChainImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
		_peer = new CommandChainImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
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

	// service
	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
		_peer.setUrl(null);
	}

	private void startService() {
		Bundle bundle = _context.getBundleContext().getBundle();
		URL url = bundle.getResource("catalog.xml");
		_peer.setUrl(url);
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public boolean execute(CommandChainContext context) throws Exception {
		context.setComponentContext(_context);
		return _peer.execute(context);
	}

}
