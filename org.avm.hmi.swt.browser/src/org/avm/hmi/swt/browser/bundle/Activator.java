package org.avm.hmi.swt.browser.bundle;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.hmi.swt.browser.BrowserIhm;
import org.avm.hmi.swt.browser.BrowserImpl;
import org.avm.hmi.swt.desktop.Desktop;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator {

	static final String PID = BrowserIhm.class.getName();

	private Logger _log;

	private ConfigurationAdmin _cm;

	private BrowserImpl _peer;

	private ConsumerImpl _consumer;

	private CommandGroupImpl _commands;

	public Activator() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_peer = new BrowserImpl();
	}

	protected void start(ComponentContext context) {
		initializeDesktop();
		initializeUserSessionService();
		initializeCommandGroup();
		startService();
		initializeConsumer();
	}

	protected void stop(ComponentContext context) {
		disposeConsumer();
		stopService();
		disposeUserSessionService();
		disposeCommandGroup();
		disposeDesktop();
	}

	// UserSessionService
	private void initializeUserSessionService() {
		UserSessionService ua = (UserSessionService) _context
				.locateService("usersession");
		_peer.setUserSessionService(ua);
	}

	private void disposeUserSessionService() {
		_peer.unsetUserSessionService(null);
	}

	// commands
	private void initializeCommandGroup() {
		_commands = new CommandGroupImpl(_context, _peer);
		_commands.start();
	}

	private void disposeCommandGroup() {
		if (_commands != null)
			_commands.stop();
	}

	// Desktop
	private void initializeDesktop() {
		Desktop base = (Desktop) _context.locateService("base");
		_peer.setBase(base);
	}

	private void disposeDesktop() {
		_peer.setBase(null);
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

	public void start() {
		_peer.start();
	}

	public void stop() {
		_peer.stop();
	}

}
