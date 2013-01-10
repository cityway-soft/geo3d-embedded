package org.avm.hmi.swt.avm.bundle;

import org.apache.log4j.Logger;
import org.avm.business.core.AvmInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.avm.Avm;
import org.avm.hmi.swt.avm.AvmImpl;
import org.avm.hmi.swt.desktop.Desktop;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Avm, UserSessionServiceInjector,AvmInjector {
	private Logger _log;

	private AvmImpl _peer;

	private ConsumerImpl _consumer;

	private ConfigurationAdmin _cm;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private static AbstractActivator _plugin;

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		//_log.setPriority(Priority.DEBUG);
		_peer = new AvmImpl();
	}

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeDesktop();
		initializeCommandGroup();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeDesktop();
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
			_log.error("initializeConfiguration error", e);
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

	// Desktop
	private void initializeDesktop() {
		Desktop base = (Desktop) _context.locateService("desktop");
		_peer.setDesktop(base);
	}

	private void disposeDesktop() {
		_peer.setDesktop(null);
	}

	// consumer
	private void initializeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer = new ConsumerImpl(_context, _peer);
			_consumer.start();
		}
	}

	private void disposeConsumer() {

		_log.debug("dispose consumer");
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

	public void notify(Object o) {
		_peer.notify(o);
	}

	// avm injector
	public void setAvm(org.avm.business.core.Avm avm) {
		_log.debug("setAvm " + avm);
		_peer.setAvm(avm);
	}

	public void unsetAvm(org.avm.business.core.Avm avm) {
		_log.debug("unset Avm");
		_peer.setAvm(null);
	}
	
	// usersession injector
	public void setUserSessionService(UserSessionService uss) {
		_log.debug("setUserSessionService = " + uss);
		_peer.setUserSessionService(uss);
	}

	public void unsetUserSessionService(UserSessionService uss) {
		_log.debug("unsetUserSessionService");
		_peer.unsetUserSessionService(null);
	}
}
