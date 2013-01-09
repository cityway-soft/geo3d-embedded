package org.avm.elementary.media.jms.bundle;

import java.util.Dictionary;

import org.apache.log4j.Logger;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Media;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.PublisherService;
import org.avm.elementary.media.jms.MediaJMS;
import org.avm.elementary.media.jms.MediaJMSImpl;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Media {

	private static Activator _plugin;

	private ConfigurationAdmin _cm;

	private MediaJMSImpl _peer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private MediaListener _messenger;

	public Activator() {
		_plugin = this;
		_peer = new MediaJMSImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeMessenger();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeCommandGroup();
		disposeMessenger();
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

	// messenger
	private void initializeMessenger() {
		_messenger = (MediaListener) _context.locateService("messenger");
		if (_peer instanceof PublisherService) {
			_peer.setMessenger(_messenger);
		}
	}

	private void disposeMessenger() {
		if (_peer instanceof PublisherService) {
			_peer.setMessenger(null);
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

	public String getMediaId() {
		return "JMS";
	}

	public int getPriority() {
		return 4;
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		if (_peer == null)
			return;
		_peer.send(header, data);
	}

}
