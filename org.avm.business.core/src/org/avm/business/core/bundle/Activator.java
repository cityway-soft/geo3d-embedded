package org.avm.business.core.bundle;

import java.util.List;

import org.avm.business.core.Avm;
import org.avm.business.core.AvmImpl;
import org.avm.business.core.AvmModel;
import org.avm.device.gps.Gps;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.database.Database;
import org.avm.elementary.geofencing.GeoFencing;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.variable.Variable;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.prefs.PreferencesService;

public class Activator extends AbstractActivator implements Avm {

	private ConfigurationAdmin _cm;

	private AvmImpl _peer;

	private ConsumerImpl _consumer;

	private ProducerImpl _producer;

	private ConfigImpl _config;

	private CommandGroupImpl _commands;

	private static AbstractActivator _plugin;

	public Activator() {
		_plugin = this;
		_peer = AvmImpl.getInstance();
		//_log.setPriority(Priority.DEBUG);
	}

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		try {
			initializeConfiguration();
			initializePreferencesService();
			initializeProducer();
			initializeOdometer();
			initializeCommandGroup();
			initializeMessenger();
			startService();
			initializeConsumer();
		} catch (Exception e) {
			_log.error("Error starting ...", e);
		}
	}

	protected void stop(ComponentContext context) {
		try {
			disposeConsumer();
			stopService();
			disposeMessenger();
			disposeCommandGroup();
			disposeOdometer();
			disposeProducer();
			disposeConfiguration();
			disposePreferencesService();
		} catch (Throwable t) {
			_log.error("Error stopping...", t);
		}
	}


	// --PreferenceAdmin
	private void initializePreferencesService() {
		PreferencesService ps = (PreferencesService) _context
				.locateService("prefs");
		_peer.setPreferencesService(ps);
	}

	private void disposePreferencesService() {
		_peer.unsetPreferencesService(null);
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
			_log.error("initializeConfiguration error", e);
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
			_consumer = new ConsumerImpl(_context, _peer);
			_consumer.start();
		}
	}

	private void disposeConsumer() {
		if (_peer instanceof ConsumerService) {
			_consumer.stop();
		}
	}

	// producer
	private void initializeProducer() {
		if (_peer instanceof ProducerService) {
			_producer = new ProducerImpl(_context);
			_producer.start();
			((ProducerService) _peer).setProducer(_producer);
		}
	}

	private void disposeProducer() {
		if (_peer instanceof ProducerService) {
			((ProducerService) _peer).setProducer(null);
			_producer.stop();
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

	// messenger
	private void initializeMessenger() {
		Messenger messenger = (Messenger) _context.locateService("messenger");
		_peer.setMessenger(messenger);
	}

	private void disposeMessenger() {
		_peer.setMessenger(null);
	}

	// odometre
	private void initializeOdometer() {
		Variable odometer = (Variable) _context.locateService("odometre");
		_peer.setOdometer(odometer);
	}

	private void disposeOdometer() {
		_peer.setOdometer(null);
	}


	public void depart() {
		_peer.depart();
	}

	public void finCourse() {
		_peer.finCourse();
	}


	public void finService() {
		_peer.finService();
	}


	public void priseCourse(int course) {
		_peer.priseCourse(course);
	}

	public void priseService(int service) {
		_peer.priseService(service);
	}

	public void sortieItineraire() {
		_peer.sortieItineraire();
	}

	public void annuler() {
		_peer.annuler();
	}

	public List getAlarm() {
		return _peer.getAlarm();
	}

	public String getProducerPID() {
		return _peer.getProducerPID();
	}

	public void prisePoste(int replaceVehicule, int replaceMatricule) {
		_peer.prisePoste(replaceVehicule, replaceMatricule);
	}

	public void entree(int balise) {
		_peer.entree(balise);
	}

	public void sortie(int balise) {
		_peer.sortie(balise);
	}

	public AvmModel getModel() {
		return _peer.getModel();
	}

	public void setGeorefMode(boolean b) {
		_peer.setGeorefMode(b);
	}
	
	public void setUserSessionService(UserSessionService uss) {
		_log.debug("setUserSessionService = " + uss);
		_peer.setUserSessionService(uss);
	}

	public void unsetUserSessionService(UserSessionService uss) {
		_log.debug("unsetUserSessionService");
		_peer.unsetUserSessionService(null);
	}
	
	public void setDatabase(Database database) {
		_log.debug("setDatabase = " + database);
		_peer.setDatabase(database);
	}

	public void unsetDatabase(Database database) {
		_log.debug("unsetDatabase");
		_peer.unsetDatabase(null);
	}

	public void setGps(Gps gps) {
		_log.debug("setDatabase = " + gps);
		_peer.setGps(gps);
	}

	public void unsetGps(Gps gps) {
		_log.debug("unsetGps");
		_peer.unsetGps(null);
	}
	
	public void setGeoFencing(GeoFencing geofencing) {
		_log.debug("setGeofencing = " + geofencing);
		_peer.setGeoFencing(geofencing);
	}

	public void unsetGeoFencing(GeoFencing geofencing) {
		_log.debug("unsetGeofencing");
		_peer.unsetGeoFencing(null);
	}
	
	public void setJdb(JDB jdb) {
		_log.debug("setJdb = " + jdb);
		_peer.setJdb(jdb);
	}

	public void unsetJdb(JDB jdb) {
		_log.debug("unsetJdb");
		_peer.unsetJdb(null);
	}

	public void setVehiculeFull(boolean b) {
		_peer.setVehiculeFull(b);
	}
	
}
