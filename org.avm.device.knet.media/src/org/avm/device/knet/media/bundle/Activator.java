package org.avm.device.knet.media.bundle;

import java.util.Dictionary;

import org.avm.device.knet.KnetAgentFactory;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.media.MediaKnet;
import org.avm.device.knet.media.MediaKnetImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.MediaListener;
import org.avm.elementary.common.PublisherService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements MediaKnet {
	static final String PID = MediaKnet.class.getName();
	private ConfigurationAdmin _cm = null;
	private CommandGroupImpl _commands = null;
	private MediaListener _messenger;

	private ConfigImpl _config = null;

	private MediaKnet _peer = null;
	private KnetAgentFactory _agentKnetFactory;

	// Dans cette version il faut faire un constructeur
	public Activator() {
		_peer = new MediaKnetImpl();
	}

	protected void start(ComponentContext context) {
		try {
			initializeConfiguration();
			initializeMessenger();
			initializeCommandGroup();
			initializeAgentKnet();
			startService();
		} catch (RuntimeException re) {
			_log.error(re.getMessage(), re);
		}
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeAgentKnet();
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

	private void initializeAgentKnet() {
		_log.debug("\tinitializeAgentKnet()");
		if (_context == null)
			return;
		if (_agentKnetFactory != null)
			return;
		_agentKnetFactory = (KnetAgentFactory) _context.locateService("knet");
		if (_peer instanceof KnetDevice) {
			((KnetDevice) _peer).setAgent(_agentKnetFactory);
		}

	}

	private void disposeAgentKnet() {
		_log.debug("\tdisposeAgentKnet()");
		if (_peer instanceof KnetDevice) {
			((KnetDevice) _peer).unsetAgent();
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

	public String getMediaId() {
		return "KNET";
	}

	public int getPriority() {
		return 4;
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		if (_peer == null)
			return;
		_peer.send(header, data);
	}

	public void setMessenger(MediaListener messenger) {
		_peer.setMessenger(messenger);
	}
}
