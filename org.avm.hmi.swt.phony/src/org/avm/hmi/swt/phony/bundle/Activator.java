package org.avm.hmi.swt.phony.bundle;

import org.apache.log4j.Logger;
import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmInjector;
import org.avm.device.phony.PhonyInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.directory.Directory;
import org.avm.elementary.directory.DirectoryInjector;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.phony.PhonyImpl;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements
		UserSessionServiceInjector, DirectoryInjector, GsmInjector,
		PhonyInjector {

	private PhonyImpl _peer;

	private ConsumerImpl _consumer;
	private ConfigImpl _config;
	private CommandGroupImpl _commands;

	private static Activator _plugin;

	private Logger _log;

	public static Activator getDefault() {
		return _plugin;
	}

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		_peer = new PhonyImpl();
	}

	protected void start(ComponentContext context) {
		initializeDesktop();
		initializeCommandGroup();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		try {
			disposeConsumer();
			stopService();
			disposeCommandGroup();
			disposeDesktop();
		} catch (Throwable t) {
			t.printStackTrace();
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
		Desktop desktop = (Desktop) _context.locateService("desktop");
		_peer.setDesktop(desktop);
	}

	private void disposeDesktop() {
		_peer.unsetDesktop(null);
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
		_log.debug("startService");
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public void start() {
		_peer.start();
	}

	public void stop() {
		_peer.stop();
	}

	public void setPhony(org.avm.device.phony.Phony phony) {
		_log.debug("set Phony");
		_peer.setPhony(phony);
	}

	public void unsetPhony(org.avm.device.phony.Phony phony) {
		_log.debug("unset Phony");
		_peer.unsetPhony(null);
	}

	public void setDirectory(Directory directory) {
		_log.debug("set Directory");
		_peer.setDirectory(directory);
	}

	public void unsetDirectory(Directory directory) {
		_log.debug("unset Directory");
		_peer.unsetDirectory(null);
	}

	public void setGsm(Gsm gsm) {
		_log.debug("set Gsm");
		_peer.setGsm(gsm);
	}

	public void unsetGsm(Gsm gsm) {
		_log.debug("unset Gsm");
		_peer.unsetGsm(null);
	}

	public void setUserSessionService(UserSessionService service) {
		_log.debug("setUserSessionService");
		_peer.setUserSessionService(service);
	}

	public void unsetUserSessionService(UserSessionService service) {
		_log.debug("unsetUserSessionService");
		_peer.unsetUserSessionService(service);
	}
	
	public void setJdb(JDB jdb) {
		_log.debug("setJdb = " + jdb);
		_peer.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_log.debug("unsetJdb");
		_peer.unsetJdb(null);
	}
}
