package org.avm.device.knet.mmi.bundle;

import org.avm.device.knet.KnetAgentFactory;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.mmi.Mmi;
import org.avm.device.knet.mmi.MmiDialogIn;
import org.avm.device.knet.mmi.MmiDialogOut;
import org.avm.device.knet.mmi.MmiImpl;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Mmi {

	static final String PID = Mmi.class.getName();

	private ConfigurationAdmin _configurationAdmin = null;

	private CommandGroupImpl _commands = null;

	private ConfigImpl _config = null;

	private KnetAgentFactory _agentFactory;

	private MmiImpl _peer = null;

	private ProducerImpl _producer = null;

	// Dans cette version il faut faire un constructeur
	public Activator() {
		_peer = new MmiImpl();
		// _log.setPriority(Priority.DEBUG);
	}

	protected void start(ComponentContext context) {
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

		_log.debug("\tinitializeConfiguration(ca)");
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
		if (_agentFactory != null)
			return;
		_agentFactory = (KnetAgentFactory) _context.locateService("knet");
		if (_peer instanceof KnetDevice) {
			((KnetDevice) _peer).setAgent(_agentFactory);
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
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).start();
		}
	}

	private void stopService() {
		if (_peer instanceof ManageableService) {
			((ManageableService) _peer).stop();
		}
	}

	public void submit(MmiDialogIn inMmiDialogIn) {
		_peer.submit(inMmiDialogIn);
	}

	public MmiDialogOut getDialogOut() {
		return _peer.getDialogOut();
	}

}
