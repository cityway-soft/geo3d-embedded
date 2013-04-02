package org.avm.hmi.swt.authentification;

import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.MessageBox;
import org.eclipse.swt.SWT;
import org.osgi.util.measurement.State;

public class AuthentificationImpl implements ManageableService, ChoiceListener,
		ConsumerService, UserSessionServiceInjector {

	private AuthentificationIhm _authIhm;

	private String _login;

	private String _password;

	private UserSessionService _session;

	private Desktop _desktop;

	private AuthentificationImpl _instance;

	private boolean _logged = false;

	public AuthentificationImpl() {
		super();
		_instance = this;
	}

	public void start() {
		_desktop.getDisplay().syncExec(new Runnable() {
			public void run() {
				_authIhm = new AuthentificationIhm(_desktop);
				_authIhm.setChoiceListener(_instance);
				_authIhm.loggedOut();
				_authIhm.setUserSession(_session);
				String vehicule = System.getProperty("org.avm.terminal.name"); //$NON-NLS-1$
				if (vehicule == null || vehicule.equals("0")) { //$NON-NLS-1$
					MessageBox.setMessage(
							Messages.getString("AuthentificationImpl.avertissement"), //$NON-NLS-1$
							Messages.getString("AuthentificationImpl.numero-parc-non-defini"), //$NON-NLS-1$
							MessageBox.MESSAGE_ALARM, SWT.CENTER);
				}
			}
		});
	}

	public void stop() {
		_authIhm.stop();
	}

	public void validation(Object obj, Object data) {
		if (data == null)
			return;
		if (obj instanceof MatriculeSelection) {
			_login = data.toString();
			_authIhm.activatePassword();
		} else if (obj instanceof PasswordSelection) {
			_password = data.toString();
			System.out.println("[AuthentificationImpl] set password :" + _password);
			if (!authentification(_login, _password)) {
				System.out.println("[AuthentificationImpl] failed");
				_authIhm
						.setErrorMessage(
								Messages
										.getString("AuthentificationImpl.matricule-ou-codesecret-incorrect"), //$NON-NLS-1$
								MessageBox.MESSAGE_WARNING);
				_authIhm.activateMatricule();
			}
			else{
				System.out.println("[AuthentificationImpl] success!!!");
				_authIhm.activateLogoutPanel(_login, (String) _session.getUserProperties().get("prenom"));
			}
		}
	}

	private boolean authentification(String matricule, String code) {
		boolean result = false;
		try {
			_session.login(matricule, code);
			result = true;
		} catch (SecurityException se) {

		}
		return result;
	}

	public void setBase(Desktop desktop) {
		_desktop = desktop;
	}

	public void loggedOn() {
		String prenom = ""; //$NON-NLS-1$
		if (_session != null && !_logged) {
			prenom = (String) _session.getUserProperties().get("prenom"); //$NON-NLS-1$
			prenom = (prenom == null) ? "" : prenom; //$NON-NLS-1$
			_desktop.setInformation(Messages
					.getString("AuthentificationImpl.bonjour") + prenom); //$NON-NLS-1$
			_authIhm.loggedOn(_login, prenom);
			_logged=true;
		}

	}

	public void loggedOut() {
		if (_logged) {
			_authIhm.loggedOut();
			_desktop.setInformation(null);
			_logged = false;
		}
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getValue() == UserSessionService.AUTHENTICATED) {
				loggedOn();
			} else {
				loggedOut();
			}
		}
	}

	public void setUserSessionService(UserSessionService service) {
		_session = service;
		_authIhm.setEnabled(true);
		_authIhm.setUserSession(service);
	}

	public void unsetUserSessionService(UserSessionService service) {
		_session = service;
		_authIhm.setEnabled(false);
	}

}
