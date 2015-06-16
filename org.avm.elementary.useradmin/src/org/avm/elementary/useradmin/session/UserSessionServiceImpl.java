package org.avm.elementary.useradmin.session;

import java.util.Dictionary;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.bundle.ConfigImpl;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.util.measurement.State;

public class UserSessionServiceImpl implements UserSessionService,
		ProducerService, ConfigurableService {

	private Logger _log;

	private User _currentUser = null;

	private ProducerManager _producer;

	private State _currentState;

	private ConfigImpl _config;

	private String[] _defaultRoles;

	private boolean _withLocalAuthentification;

	private Dictionary _anonymousUserProperties;

	private UserAdmin userAdminService;

	public UserSessionServiceImpl() {
		_log = Logger.getInstance(this.getClass());
		_currentState = STATE_NOT_AUTHENTICATED;
	}

	public void login(String matricule, String codesecret)
			throws SecurityException {
		_currentUser = null;

		User user = getUserAdminService().getUser(MATRICULE, matricule);
		if (user == null) {
			if (_withLocalAuthentification) {
				_log.info("Mode AVEC controle local d'authentification : user="
						+ user);
				throw new SecurityException("No such user");
			} else {
				_log.info("Mode sans controle local d'authentification");
				_anonymousUserProperties = new Properties();
				_anonymousUserProperties.put(MATRICULE, matricule);
				_anonymousUserProperties.put(NOM, "anonymous");
				_anonymousUserProperties.put(PRENOM, "anonymous");
			}
		} else {
			if (!user.hasCredential(CODESECRET, codesecret)) {
				throw new SecurityException("Invalid password");
			}
		}

		_currentUser = user;
		_currentState = STATE_AUTHENTICATED;
		_log.info(_currentState);
		_producer.publish(_currentState);
	}

	public void logout() {
		_currentState = STATE_NOT_AUTHENTICATED;
		_producer.publish(_currentState);
		_log.info(_currentState);
		_currentUser = null;
		_anonymousUserProperties = null;
	}

	public Dictionary getUserProperties() {
		if (_currentUser != null) {
			return _currentUser.getProperties();
		}
		if (!_withLocalAuthentification) {
			return _anonymousUserProperties;
		}

		return null;

	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
		if (_producer != null) {
			_producer.publish(_currentState);
		}
	}

	public boolean hasRole(String role) {
		if (role == null) {
			return false;
		}
		if (_currentUser != null) {
			Authorization auth = getUserAdminService().getAuthorization(
					_currentUser);
			return auth.hasRole(role);
		}
		if (!_withLocalAuthentification) {
			for (int i = 0; i < _defaultRoles.length; i++) {
				if (role.equals(_defaultRoles[i])) {
					return true;
				}

			}
		}
		return false;
	}

	public void setUserAdmin(UserAdmin ua) {
		if (ua != null) {

		}
		userAdminService = ua;
	}

	public UserAdmin getUserAdminService() {
		return userAdminService;
	}

	public State getState() {
		return _currentState;
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
		_defaultRoles = _config.getDefaultRoles();
		_withLocalAuthentification = _config.isAuthentication();
	}

	public void setAuthentification(boolean b) {
		_withLocalAuthentification = b;
		_log.info("Set Authentification mode to :" + _withLocalAuthentification);
		logout();

	}

}
