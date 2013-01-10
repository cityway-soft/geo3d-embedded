package org.avm.device.knet.bundle;

import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetAgentFactory;
import org.avm.device.knet.KnetAgentFactoryImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements KnetAgentFactory {

	static final String PID = KnetAgent.class.getName();

	private ConfigurationAdmin _configurationAdmin = null;

	private CommandGroupImpl _commands = null;

	private ConfigImpl _config = null;

	private KnetAgentFactoryImpl _peer = null;

	// private ProducerImpl _producer=null;

	// Dans cette version il faut faire un constructeur
	public Activator() {
		_peer = new KnetAgentFactoryImpl();
		// _log.setPriority(Priority.DEBUG);
		_log.debug("activator");
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializeCommandGroup();
		startService();
	}

	protected void stop(ComponentContext context) {
		disposeConfiguration();
		disposeCommandGroup();
		stopService();
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

	// Comment rendre le service configurable :
	private void initializeConfiguration() {
		_log.debug("initializeConfiguration()");
		if (_context == null)
			return;
		if (_configurationAdmin != null)
			return;
		if (_config != null)
			return;

		_configurationAdmin = (ConfigurationAdmin) _context.locateService("cm");
		_config = new ConfigImpl(_context, _configurationAdmin);
		_config.start();
		if (_peer instanceof ConfigurableService) {
			((ConfigurableService) _peer).configure(_config);
		}
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

	// Comment faire que des commandes soient accessibles par une console
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

	public KnetAgent create(int typeApp) {
		return _peer.create(typeApp);
	}
}
