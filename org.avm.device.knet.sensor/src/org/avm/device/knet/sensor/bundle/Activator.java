package org.avm.device.knet.sensor.bundle;

import org.avm.device.knet.KnetAgentFactory;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.sensor.Sensor;
import org.avm.device.knet.sensor.SensorImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Sensor {
	static final String PID = Sensor.class.getName();
	private ConfigurationAdmin _configurationAdmin = null;
	private CommandGroupImpl _command = null;

	private ConfigImpl _config = null;

	private Sensor _peer = null;
	private KnetAgentFactory _agentFacrory;

	public Activator() {
		_peer = new SensorImpl();
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		initializeAgentKnet();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeAgentKnet();
		disposeConfiguration();
		disposeCommandGroup();
	}

	// config
	private void initializeConfiguration() {
		if (_configurationAdmin == null) {
			_log.debug("\tinitializeConfiguration");
			initializeConfiguration((ConfigurationAdmin) _context
					.locateService("cm"));
		}
	}

	private void disposeConfiguration() {
		_log.debug("\tdisposeConfiguration");
		if (_config != null)
			_config.stop();
		if (_peer instanceof ConfigurableService) {
			((ConfigurableService) _peer).configure(null);
		}
		_configurationAdmin = null;
		_config = null;
	}

	/**
	 * Appele par DS (voir fichier activator.xml, balise reference), quand la
	 * dite reference devient disponible.
	 * 
	 * @param ca
	 *            Objet ConfigurationAdmin qui vient d'apparaitre sur le
	 *            framework.
	 */
	protected void initializeConfiguration(ConfigurationAdmin ca) {
		if (_context == null)
			return;
		if (_configurationAdmin != null)
			return;
		if (_config != null)
			return;

		_log.debug("\tinitializeConfiguration");
		_configurationAdmin = ca;
		_config = new ConfigImpl(_context, _configurationAdmin);
		_config.start();
		if (_peer instanceof ConfigurableService) {
			((ConfigurableService) _peer).configure(_config);
		}
	}

	/**
	 * Appele par DS (voir fichier activator.xml, balise reference), quand la
	 * dite reference devient indisponible.
	 * 
	 * @param ca
	 *            Objet ConfigurationAdmin qui vient de disparaitre du
	 *            framework.
	 */
	protected void disposeConfiguration(ConfigurationAdmin ca) {
		_log.debug("\tdisposeConfiguration");
		disposeConfiguration();
	}

	// commands
	private void initializeCommandGroup() {
		_log.debug("\tinitializeCommandGroup");
		_command = new CommandGroupImpl(_context, _peer, _config);
		_command.start();
	}

	private void disposeCommandGroup() {
		_log.debug("\tdisposeCommandGroup");
		if (_command != null)
			_command.stop();
		_command = null;
	}

	private void initializeAgentKnet() {
		_log.debug("\tinitializeAgentKnet()");
		if (_context == null)
			return;
		if (_agentFacrory != null)
			return;
		_agentFacrory = (KnetAgentFactory) _context.locateService("knet");
		if (_peer instanceof KnetDevice) {
			((KnetDevice) _peer).setAgent(_agentFacrory);
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
		_log.debug("Arret de " + this.getClass().getName());
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
	}

	private void startService() {
		_log.debug("Demarrage de " + this.getClass().getName());
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	public void beep(int inDuration) {
		_peer.beep(inDuration);
	}

	public String readCompost() {
		return _peer.readCompost();
	}

	public String readFrontDoor() {
		return _peer.readFrontDoor();
	}

	public String readRearDoor() {
		return _peer.readRearDoor();
	}

}
