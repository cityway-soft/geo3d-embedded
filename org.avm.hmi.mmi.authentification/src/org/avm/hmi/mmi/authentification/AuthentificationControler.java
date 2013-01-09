package org.avm.hmi.mmi.authentification;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.business.core.AvmModel;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.session.UserSessionManager;
import org.avm.hmi.mmi.application.actions.Keys;
import org.avm.hmi.mmi.application.actions.ProcessCustomizer;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;
import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.application.screens.Messages;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4Attente;

public class AuthentificationControler implements ManageableService,
		ProcessCustomizer {

	private AuthentificationView _authView;

	private UserSessionManager _session;

	private AVMDisplay _base;

	private Logger _log;

	/*
	 * States.ETAT_AUTH devient AvmModel.STRING_STATE_INITIAL
	 */

	public AuthentificationControler() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
	}

	public void start() {
		_log.debug("Starting AuthentificationControler");
		_authView = AuthentificationView.createInstance(_base);
		_authView.open();
		_authView.loggedOut(AvmModel.STRING_STATE_INITIAL);
		addProcess();
		_log.debug("AuthentificationControler started");
	}

	public void stop() {
		_log.debug("Stopping AuthentificationControler");
		_authView.close();
		_log.debug("AuthentificationControler stopped");
	}

	public void addProcess() {
		_base.setProcess(Keys.KEY_V, AvmModel.STRING_STATE_INITIAL,
				new OnValidation());
		_base.setProcess(Keys.KEY_C, AvmModel.STRING_STATE_INITIAL,
				new OnCancel());
		_base.setProcess(Keys.KEY_BACK,
				AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE, new OnCancel());
	}

	public void setUserSession(UserSessionManager us) {
		_session = us;
	}

	public void loggedOn() {
		_log.debug("loggedOn");
		_base.clearMessages();
		_base.startAttente();
	}

	public void loggedOut() {
		_log.debug("loggedOut");
		_session.logout();
		_authView.loggedOut(AvmModel.STRING_STATE_INITIAL);
	}

	public void setBase(AVMDisplay base) {
		_base = base;
	}

	private class OnValidation implements ProcessRunnable {
		private String _matricule;
		private boolean _result = false;

		public void init(String[] args) {
			_matricule = args[0];
		}

		public void run() {
			try {
				_log.debug("Demande de login de " + _matricule);
				loggedOn();// Doit etre avant l'action !!!!
				_session.login(_matricule, "");
				_result = true;
			} catch (SecurityException se) {
				_result = false;
				_base.setMessage(Messages.getString("auth.matricule")
						+ _matricule + Messages.getString("auth.incorrect"));
				loggedOut();
			}
		}

		public boolean isValid() {
			return _result;
		}
	}

	private class OnCancel implements ProcessRunnable {

		public void init(String[] args) {
		}

		public void run() {
			loggedOut();
		}

	}

}
