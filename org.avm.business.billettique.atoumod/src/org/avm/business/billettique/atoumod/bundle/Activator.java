package org.avm.business.billettique.atoumod.bundle;

import org.avm.business.billettique.atoumod.Billettique;
import org.avm.business.billettique.atoumod.BillettiqueImpl;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Billettique,
		AvmInjector {
	private static Activator _plugin;
	private ConfigImpl _config;
	private ConsumerImpl _consumer;
	private CommandGroupImpl _commands;
	private BillettiqueImpl _peer;

	public Activator() {
		_plugin = this;
		_peer = new BillettiqueImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeConsumer();
		initializeCommandGroup();
		initializeAlarmService();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeAlarmService();
		disposeCommandGroup();
		disposeConsumer();
		disposeConfiguration();
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

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}

	// alarm service
	private void initializeAlarmService() {
		AlarmService alarmeService = (AlarmService) _context
				.locateService("alarm");
		_peer.setAlarmService(alarmeService);
	}

	private void disposeAlarmService() {
		_peer.setAlarmService(null);
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

	public void setAvm(org.avm.business.core.Avm avm) {
		_log.debug("setAvm " + avm);
		_peer.setAvm(avm);
	}

	public void unsetAvm(Avm avm) {
		_peer.unsetAvm(null);
	}

	public void setEnable(boolean b) {
		_peer.setEnable(b);
	}

}
