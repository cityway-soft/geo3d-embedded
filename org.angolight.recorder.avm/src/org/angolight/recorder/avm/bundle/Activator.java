package org.angolight.recorder.avm.bundle;

import org.angolight.indicator.Indicator;
import org.angolight.indicator.IndicatorInjector;
import org.angolight.recorder.Recorder;
import org.angolight.recorder.avm.RecorderService;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Recorder,
		IndicatorInjector, JDBInjector {
	private ConfigurationAdmin _cm;

	private RecorderService _peer;

	private ConsumerImpl _consumer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private static Activator _plugin;

	public Activator() {
		_plugin = this;
		_peer = new RecorderService();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeConsumer();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		try {
			stopService();
			disposeCommandGroup();
			disposeConsumer();
			disposeConfiguration();
		} catch (Throwable t) {
			t.printStackTrace();
		}
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

	public void setIndicator(Indicator indicator) {
		_peer.setIndicator(indicator);
	}

	public void unsetIndicator(Indicator indicator) {
		_peer.unsetIndicator(indicator);
	}

	public void setJdb(JDB jdb) {
		_peer.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_peer.unsetJdb(jdb);
	}

}
