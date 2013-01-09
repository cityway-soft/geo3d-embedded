/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

import org.avm.hmi.mmi.application.display.AVMDisplay;

import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * @author lbr Voir bdd/content/win/ATTENTE.xml <container name="ATTENTE"
 *         menu="MAIN"> <clock x="204" y="0" w="36" h="10" font="FNT_7B"
 *         name="time" bg="BLACK" fg="WHITE"/> <!-- saisie cachee --> <label
 *         x="0" y="0" w="1" h="1" font="" name="passwd" bg="BLACK" fg="BLACK"
 *         edit="num" max="4"/> <!-- fin saisie cachee -->
 * 
 * <label x="0" y="10" w="240" h="18" font="FNT_9B" name="text"
 * textid="INF_INIT_ENCOURS"/> <label x="0" y="25" w="240" h="18" font="FNT_9B"
 * name="progress" textid=""/>
 * 
 * <label x="0" y="34" w="240" h="8" font="FNT_7S" name="msg1" textid=""/>
 * <label x="0" y="44" w="240" h="8" font="FNT_7S" name="msg2" textid=""/>
 * 
 * <!-- V pour valider ou C pour corriger--> <label x="1" y="1" w="1" h="1"
 * font="FNT_8S" name="16" halign="left"/>
 * 
 * <!--Les softKeys F1 a F5--> <label x="0" y="54" w="44" h="10" font="FNT_7S"
 * bg="BLACK" fg="WHITE" name="2"/> </container>
 */
public class MmiDialogInWrapper4Attente extends MmiDialogInWrapper {
	// Singleton
	private static MmiDialogInWrapper4Attente INSTANCE = null;

	private MmiDialogInWrapper4Attente() {
		super();
	}

	public synchronized static MmiDialogInWrapper4Attente getNewInstance(
			String etat) {
		if (INSTANCE == null) {
			INSTANCE = new MmiDialogInWrapper4Attente();
			// _progressor = INSTANCE.new Progressor();
			// _executor = new ThreadedExecutor();
		}
		INSTANCE.reset();
		INSTANCE.setDialogName(MmiConstantes.PROGRESSBAR);
		INSTANCE.setAppId(etat);
		return INSTANCE;
	}

	private static final int TAILLE_FEN = 40;
	private static final String PROGRESS = "progress";

	private char[] _progressBar = new char[TAILLE_FEN];
	private int _progression = 0;

	public String abstractGetLabel4Key(String key) {
		if (key.equalsIgnoreCase(F1))
			return "2";
		return null;
	}

	public void progress() {
		if (_progression >= TAILLE_FEN) {
			_progression = 0;
			for (int i = 0; i < TAILLE_FEN; i++)
				_progressBar[i] = '-';
		}
		_progressBar[_progression++] = '|';
	}

	public void abstractGetDialogIn() {
		setLabelValue(PROGRESS, new String(_progressBar));
	}

	protected void reset() {
		for (int i = 0; i < TAILLE_FEN; i++)
			_progressBar[i] = '-';
		_progression = 0;
	}

	public void resetProgress() {
		reset();
	}
}
