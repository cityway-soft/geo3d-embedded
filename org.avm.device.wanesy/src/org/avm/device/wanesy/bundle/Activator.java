package org.avm.device.wanesy.bundle;

import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.wanesy.DeviceWanesy;
import org.avm.device.wanesy.DeviceWanesyImpl;
import org.avm.device.wanesy.DeviceWanesyListener;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements DeviceWanesy {

	private ConfigurationAdmin _cm;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private DeviceWanesyImpl _peer;

	static final String PID = DeviceWanesy.class.getName();

	protected void start(ComponentContext context) {
		//initializeConfiguration();
		//initializeCommandGroup();
		startService();
	}

	// service
	private void startService() {
		_peer = new DeviceWanesyImpl();
		_peer.start();
	}

	private void stopService() {
		_peer.stop();
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

	

	public boolean isConnected() {
		if (_peer == null)
			return false;
		return _peer.isConnected();
	}

	public boolean send(KmsMarshaller kms) {
		if (_peer == null)
			return false;
		return _peer.send(kms);
	}

	public void removeDeviceListener(
			DeviceWanesyListener deviceWanesyInterfaceService) {
		_peer.removeDeviceListener(deviceWanesyInterfaceService);
	}

	public void addDeviceListener(
			DeviceWanesyListener deviceWanesyInterfaceService) {
		_peer.addDeviceListener(deviceWanesyInterfaceService);
	}
}
