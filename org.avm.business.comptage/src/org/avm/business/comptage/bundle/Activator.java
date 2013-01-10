package org.avm.business.comptage.bundle;

import org.avm.business.comptage.Comptage;
import org.avm.business.comptage.ComptageImpl;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.device.comptage.ComptageInjector;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator implements Comptage, ComptageInjector,AvmInjector, JDBInjector {

	private static Activator _plugin;
	private ConfigurationAdmin _cm;
	private ConfigImpl _config;
	private ComptageImpl _peer;
	private ConsumerImpl _consumer;
	private CommandGroupImpl _commands;

	public Activator() {
		_plugin = this;
		_peer = new ComptageImpl();
	}

	public static Activator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		try {
			initializeConfiguration();
			initializeConsumer();
			initializeCommandGroup();
			initializeComptage();
			//initializeAvm();
			startService();
		} catch (RuntimeException re) {
			_log.error("error starting", re);
		}
	}

	protected void stop(ComponentContext context) {
		stopService();
		disposeComptage();
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
	
	// afficheur
	private void initializeComptage() {
		org.avm.device.comptage.Comptage comptage = (org.avm.device.comptage.Comptage) _context
				.locateService("comptage");
		_log.info("initializeComptage: "+comptage);
		if (_peer instanceof ComptageInjector) {
			((ComptageInjector) _peer).setComptage(comptage);
		}
	}

	private void disposeComptage() {
		if (_peer instanceof ComptageInjector) {
			((ComptageInjector) _peer).setComptage(null);
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
	
	public void setAvm(org.avm.business.core.Avm avm) {
		_log.debug("setAvm " + avm);
		_peer.setAvm(avm);
	}

	public void unsetAvm(Avm avm) {
		_log.debug("unset Avm");
		_peer.unsetAvm(null);
	}

		public void setComptage(org.avm.device.comptage.Comptage comptePassagers) {
		_peer.setComptage(comptePassagers);
	}

	public void unsetComptage(org.avm.device.comptage.Comptage comptePassagers) {
		_peer.unsetComptage(comptePassagers);
	}

	public void setJdb(JDB jdb) {
		_peer.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_peer.unsetJdb(jdb);
	}

}
