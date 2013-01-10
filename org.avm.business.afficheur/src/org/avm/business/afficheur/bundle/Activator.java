package org.avm.business.afficheur.bundle;

import org.avm.business.afficheur.Afficheur;
import org.avm.business.afficheur.AfficheurImpl;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.messages.Messages;
import org.avm.business.messages.MessagesInjector;
import org.avm.device.afficheur.AfficheurInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Afficheur, AfficheurInjector,AvmInjector {

	private static Activator _plugin;
	private ConfigurationAdmin _cm;
	private ConfigImpl _config;
	private AfficheurImpl _peer;
	private ConsumerImpl _consumer;
	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
		_peer = new AfficheurImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		try {
			initializeConfiguration();
			initializeConsumer();
			initializeCommandGroup();
			//initializeAvm();
			initializeMessages();
			initializeAfficheur();
			startService();
		} catch (RuntimeException re) {
			_log.error("error starting", re);
		}
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeAfficheur();
		disposeMessages();
		disposeCommandGroup();
		//disposeAvm();
		disposeConsumer();
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

	// consumer
	private void initializeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer = new ConsumerImpl(_context, (ConsumerService) _peer);
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

	// avm
//	private void initializeAvm() {
//		if (_peer instanceof AvmInjector) {
//			Avm avm = (Avm) _context.locateService("avm");
//			((AvmInjector) _peer).setAvm(avm);
//		}
//	}
//
//	private void disposeAvm() {
//		if (_peer instanceof AvmInjector) {
//			Avm avm = (Avm) _context.locateService("avm");
//			((AvmInjector) _peer).unsetAvm(avm);
//		}
//	}

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

	// afficheur
	private void initializeAfficheur() {
		org.avm.device.afficheur.Afficheur afficheur = (org.avm.device.afficheur.Afficheur) _context
				.locateService("afficheur");
		if (_peer instanceof AfficheurInjector) {
			((AfficheurInjector) _peer).setAfficheur(afficheur);
		}
	}

	private void disposeAfficheur() {
		if (_peer instanceof AfficheurInjector) {
			((AfficheurInjector) _peer).setAfficheur(null);
		}
	}
	
	// messages
	private void initializeMessages() {
		if (_peer instanceof MessagesInjector) {
			Messages messages = (Messages) _context.locateService("messages");
			((MessagesInjector) _peer).setMessages(messages);
		}
	}

	private void disposeMessages() {
		if (_peer instanceof MessagesInjector) {
			Messages messages = (Messages) _context.locateService("messages");
			((MessagesInjector) _peer).setMessages(messages);
		}
	}
	
	public void setAvm(org.avm.business.core.Avm avm) {
		_log.debug("setAvm " + avm);
		_peer.setAvm(avm);
	}

	public void unsetAvm(Avm avm) {
		_log.debug("unset Avm");
		_peer.unsetAvm(null);
	}

	public void setAfficheur(org.avm.device.afficheur.Afficheur afficheur) {
		_log.debug("setAfficheur " + afficheur);
		_peer.setAfficheur(afficheur);
	}

	public void unsetAfficheur(org.avm.device.afficheur.Afficheur afficheur) {
		_log.debug("unsetAfficheur ");
		_peer.unsetAfficheur(afficheur);
	}

}
