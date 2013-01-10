package org.avm.hmi.mmi.authentification;

import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4Attente;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4Saisie;

public class AuthentificationView implements Authentification {
	// Singleton
	private static AuthentificationView INSTANCE = null;

	private AuthentificationView() {
	}

	public synchronized static AuthentificationView createInstance(
			AVMDisplay base) {
		if (INSTANCE == null)
			INSTANCE = new AuthentificationView();
		INSTANCE.setBase(base);
		return INSTANCE;
	}

	private AVMDisplay _base = null;
	private MmiDialogInWrapper4Saisie _screen = null;

	public void open() {
	}

	public void close() {
		_base.stopAttente();
	}

	private void setBase(AVMDisplay base) {
		_base = base;
	}

	public void loggedOut(String state) {
		_screen = MmiDialogInWrapper4Saisie.getNewInstance(state);
		_screen.clearSoftKeys();
		_screen.setPrompt(MmiDialogInWrapper4Saisie.MATRICULE);
		_base.setScreen2fg(_screen);
	}

}
