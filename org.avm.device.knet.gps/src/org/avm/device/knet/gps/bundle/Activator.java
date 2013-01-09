package org.avm.device.knet.gps.bundle;

import org.avm.device.gps.Gps;
import org.avm.device.knet.KnetAgentFactory;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.gps.GpsImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.position.Position;

public class Activator extends AbstractActivator implements Gps {
	static final String PID = Gps.class.getName();

	private ConfigurationAdmin _configurationAdmin = null;

	private CommandGroupImpl _commands = null;

	private ConfigImpl _config = null;

	private GpsImpl _peer = null;

	private ProducerImpl _producer = null;

	private KnetAgentFactory _agentKnetFactory;

	public Activator() {
		// _peer = (Gps) new GpsImpl(Knet.KNETDHOST,
		// Knet.KNETDPORT,
		// Knet.AUTH_login,
		// Knet.AUTH_passwd,
		// Knet.GPS_APP);
		_peer = new GpsImpl();
		// _log.setPriority(Priority.DEBUG);
	}

	protected void start(ComponentContext context) {
		// if (true){
		// throw new RuntimeException("texte debile");
		// }

		initializeConfiguration();
		initializeProducer();
		initializeCommandGroup();
		initializeAgentKnet();
		startService();
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeAgentKnet();
		disposeConfiguration();
		disposeProducer();
		disposeCommandGroup();
	}

	// Comment rendre le service configurable :
	private void initializeConfiguration() {
		_log.debug("initializeConfiguration()");
		initializeConfiguration((ConfigurationAdmin) _context
				.locateService("cm"));
	}

	private void disposeConfiguration() {
		_log.debug("disposeConfiguration()");
		if (_config != null) {
			_config.stop();
			if (_peer instanceof ConfigurableService) {
				((ConfigurableService) _peer).configure(null);
			}
		}
		_configurationAdmin = null;
		_config = null;
	}

	protected void initializeConfiguration(ConfigurationAdmin ca) {
		if (_context == null)
			return;
		if (_configurationAdmin != null)
			return;
		if (_config != null)
			return;

		_log.debug("\tinitializeConfiguration(ca)");
		_configurationAdmin = ca;
		_config = new ConfigImpl(_context, _configurationAdmin);
		_config.start();
		if (_peer instanceof ConfigurableService) {
			((ConfigurableService) _peer).configure(_config);
		}
	}

	protected void disposeConfiguration(ConfigurationAdmin ca) {
		_log.debug("\tdisposeConfiguration(ca)");
		disposeConfiguration();
	}

	private void initializeCommandGroup() {
		_log.debug("initializeCommandGroup");
		_commands = new CommandGroupImpl(_context, _peer, _config);
		_commands.start();
	}

	private void disposeCommandGroup() {
		_log.debug("disposeCommandGroup");
		if (_commands != null) {
			_commands.stop();
		}
		_commands = null;
	}

	// producer
	private void initializeProducer() {
		if (_producer == null) {
			_log.debug("\tinitializeProducer");
			_producer = new ProducerImpl(_context);
			_producer.start();
			if (_peer instanceof ProducerService)
				((ProducerService) _peer).setProducer(_producer);
		}
	}

	private void disposeProducer() {
		_log.debug("\tdisposeProducer");
		if (_peer instanceof ProducerService)
			((ProducerService) _peer).setProducer(null);
		if (_producer != null)
			_producer.stop();
		_producer = null;
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

	// Interfaces pour gerer le service ...
	private void startService() {
		_log.debug("startService " + PID);
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	private void stopService() {
		_log.debug("Unregistering " + PID);
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
	}

	public Position getCurrentPosition() {
		return _peer.getCurrentPosition();
	}

	public void setDebugOn() {
		_peer.setDebugOn();
	}

	public void unsetDebugOn() {
		_peer.unsetDebugOn();
	}
}
