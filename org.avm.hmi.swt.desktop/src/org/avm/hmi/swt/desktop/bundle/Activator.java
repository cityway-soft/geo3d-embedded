package org.avm.hmi.swt.desktop.bundle;

import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.hmi.swt.application.display.AVMDisplay;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Desktop {
	private static Activator _plugin;

	static final String PID = Desktop.class.getName();

	private DesktopImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private ConsumerImpl _consumer;

	public Activator() {
		_peer = new DesktopImpl();
		_plugin = this;
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		initializeConsumer();
		initializeDisplay();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeDisplay();
		disposeConsumer();
		disposeCommandGroup();
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

	// display
	private void initializeDisplay() {
		AVMDisplay avmDislay = (AVMDisplay) _context.locateService("display");
		_peer.setDisplay(avmDislay.getDisplay());

	}

	private void disposeDisplay() {
		_peer.setDisplay(null);
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

	public void addTabItem(String string, Composite avmihm) {
		_peer.addTabItem(string, avmihm);
	}

	public void addTabItem(String string, Composite avmihm, int idx) {
		_peer.addTabItem(string, avmihm, idx);
	}

	public Display getDisplay() {
		return _peer.getDisplay();
	}

	public Composite getMainPanel() {
		return _peer.getMainPanel();
	}

	public Composite getRightPanel() {
		return _peer.getRightPanel();
	}

	public void setMessageBox(String title, String string, int b) {
		_peer.setMessageBox(title, string, b);
	}

	public void setInformation(String msg) {
		_peer.setInformation(msg);
	}

	public void removeTabItem(String string) {
		_peer.removeTabItem(string);
	}

	public void activateItem(String name) {
		_peer.activateItem(name);
	}

	public Object[] getItems() {
		return _peer.getItems();
	}

	public void setTabItemImage(String name, Image image) {
		_peer.setTabItemImage(name, image);
	}

	public void setFavorite(String name) {
		_peer.setFavorite(name);
	}

}
