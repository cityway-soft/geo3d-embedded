package org.avm.elementary.useradmin.bundle;

import java.util.Dictionary;

import org.apache.log4j.Logger;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.core.PreferencesServiceInjector;
import org.avm.elementary.useradmin.manager.UserAdminControler;
import org.avm.elementary.useradmin.manager.UserAdminManagerImpl;
import org.avm.elementary.useradmin.manager.UserDataManager;
import org.avm.elementary.useradmin.session.UserSessionServiceImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.util.measurement.State;

public class Activator extends AbstractActivator implements UserSessionService,
		UserAdminControler {

	static final String PID = UserSessionService.class.getName();

	private static AbstractActivator _plugin;

	private Logger _log;

	private ConfigurationAdmin _cm;

	private UserAdminManagerImpl _peerUserAdminManager;

	private UserSessionServiceImpl _peerUserSession;

	private ConfigImpl _config;

	private ManagerCommandGroupImpl _managerCommands;

	private SessionCommandGroupImpl _sessionCommands;

	private UserDataManager _manager;

	private org.avm.elementary.useradmin.bundle.ProducerImpl _producer;

	public Activator() {
		_plugin = this;
		_log = Logger.getInstance(this.getClass());
		_peerUserAdminManager = new UserAdminManagerImpl();
		_peerUserAdminManager.setUserAdminControler(this);
		_peerUserSession = new UserSessionServiceImpl();
	}

	public static AbstractActivator getDefault() {
		return _plugin;
	}

	protected void start(ComponentContext context) {
		initializeConfiguration();
		initializePreferencesService();
		initializeCommandGroup();
		initializeManager();
		initializeProducer();
		startService();
	}

	protected void stop(ComponentContext context) {
		logout();
		stopService();
		disposeProducer();
		disposeManager();
		disposeCommandGroup();
		disposePreferencesService();
		disposeConfiguration();
	}

	// producer
	private void initializeProducer() {
		if (_peerUserSession instanceof ProducerService) {
			_producer = new ProducerImpl(_peerUserSession, _context);
			_producer.start();
			((ProducerService) _peerUserSession).setProducer(_producer);
		}
	}

	private void disposeProducer() {
		if (_peerUserSession instanceof ProducerService) {
			((ProducerService) _peerUserSession).setProducer(null);
			_producer.stop();
		}
	}

	// config
	private void initializeConfiguration() {
		_cm = (ConfigurationAdmin) _context.locateService("cm");
		try {
			_config = new ConfigImpl(_context, _cm);
			_config.start();
			if (_peerUserAdminManager instanceof ConfigurableService) {
				((ConfigurableService) _peerUserAdminManager)
						.configure(_config);
			}
			if (_peerUserSession instanceof ConfigurableService) {
				((ConfigurableService) _peerUserSession).configure(_config);
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	private void disposeConfiguration() {
		_config.stop();
		if (_peerUserAdminManager instanceof ConfigurableService) {
			((ConfigurableService) _peerUserAdminManager).configure(null);
		}
	}

	// --PreferenceAdmin
	private void initializePreferencesService() {
		PreferencesService ps = (PreferencesService) _context
				.locateService("prefs");

		if (_peerUserAdminManager instanceof PreferencesServiceInjector) {
			((PreferencesServiceInjector) _peerUserAdminManager)
					.setPreferencesService(ps);
		}
	}

	private void disposePreferencesService() {
		if (_peerUserAdminManager instanceof PreferencesServiceInjector) {
			((PreferencesServiceInjector) _peerUserAdminManager)
					.setPreferencesService(null);
		}
		if (_peerUserSession instanceof PreferencesServiceInjector) {
			((PreferencesServiceInjector) _peerUserSession)
					.setPreferencesService(null);
		}
	}

	private String getEquinoxUserAdminPreferenceFilename() {
		long bundleId = -1;
		ServiceReference sr = _context.getBundleContext().getServiceReference(
				"org.osgi.service.useradmin.UserAdmin");
		if (sr != null) {
			Bundle bundle = sr.getBundle();
			bundleId = bundle.getBundleId();

		}
		StringBuffer buf = new StringBuffer();

		buf.append(System.getProperty("osgi.configuration.area"));
		buf.append(System.getProperty("file.separator"));
		buf.append(".settings");
		buf.append(System.getProperty("file.separator"));
		buf.append("org.eclipse.core.runtime.preferences.OSGiPreferences.");
		buf.append(bundleId);
		buf.append(".prefs");
		return buf.toString();
	}

	// commands
	private void initializeCommandGroup() {
		_managerCommands = new ManagerCommandGroupImpl(_context,
				_peerUserAdminManager, _config);
		_managerCommands.start();

		_sessionCommands = new SessionCommandGroupImpl(_context,
				_peerUserSession, _config);
		_sessionCommands.start();
	}

	private void disposeCommandGroup() {
		if (_managerCommands != null) {
			_managerCommands.stop();
		}
		if (_sessionCommands != null) {
			_sessionCommands.stop();
		}
	}

	// service
	private void stopService() {
		if (_peerUserAdminManager instanceof ManageableService) {
			((ManageableService) _peerUserAdminManager).stop();
		}
	}

	private void startService() {
		if (_peerUserAdminManager instanceof ManageableService) {
			String preferenceFilename = getEquinoxUserAdminPreferenceFilename();
			_peerUserAdminManager
					.setUserAdminPreferenceFilename(preferenceFilename);
			((ManageableService) _peerUserAdminManager).start();
		}
	}

	// manager
	private void initializeManager() {
		try {
			_manager = new UserDataManager(_context.getBundleContext(), _config);
			_manager.setPeer(_peerUserAdminManager);
			_manager.start();
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	private void disposeManager() {
		if (_manager != null) {
			_manager.setPeer(null);
			_manager.stop();
		}
	}

	public Dictionary getUserProperties() {
		return _peerUserSession.getUserProperties();
	}

	public void login(String matricule, String codesecret) {
		_peerUserSession.login(matricule, codesecret);
	}

	public void logout() {
		_peerUserSession.logout();
	}

	public boolean hasRole(String role) {
		return _peerUserSession.hasRole(role);
	}

	public void reinitMembers() throws Throwable {
		_peerUserAdminManager.reinitMembers();
	}

	public void removeAllUsers() throws Throwable {
		_peerUserAdminManager.removeAllUsers();
	}

	public void restart() {
		Bundle bundleUA = null;

		
		_log.info("Restaring useradmin equinox.... ");
		_log.info("_context=" + _context);
		_log.info("getBundleContext()=" + _context.getBundleContext());
		Bundle[] bundles = _context.getBundleContext().getBundles();
		_log.info("getBundles=" + bundles);
		for (int i = 0; i < bundles.length; i++) {
			String sb = bundles[i].getSymbolicName();
			if (sb.equals("org.eclipse.equinox.useradmin")) {
				bundleUA = bundles[i];
				break;
			}
		}

		if (bundleUA == null) {
			_log.error("Unable to find bundle org.eclipse.equinox.useradmin ! ");
			return;

		}

		try {
			bundleUA.stop();
		} catch (BundleException e) {
		}

		int cpt = 5;
		do {
			try {
				bundleUA.start();
				if (bundleUA.getState() == Bundle.ACTIVE) {
					break;
				}
			} catch (BundleException e) {
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			cpt--;
		} while (cpt > 0 && bundleUA.getState() != Bundle.ACTIVE);

	}

	// public UserAdmin getUserAdminService() {
	// return (UserAdmin) _context.locateService("useradmin");
	// }

	public State getState() {
		return _peerUserSession.getState();
	}

	public void addUserAdminService(UserAdmin ua) {
		_log.debug("add UserAdmin service = " + ua);
		_peerUserAdminManager.setUserAdmin(ua);
		_peerUserSession.setUserAdmin(ua);
	}

	public void removeUserAdminService(UserAdmin ua) {
		_log.debug("remove UserAdmin");
		_peerUserAdminManager.setUserAdmin(null);
		_peerUserSession.setUserAdmin(null);
	}

}
