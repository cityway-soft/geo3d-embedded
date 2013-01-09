/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

/**
 * @author lbr *Voir bdd/content/win/ARRET.xml <?xml version="1.0"
 *         encoding="UTF-8"?> <container name="ARRET" menu="MAIN"> <clock
 *         x="205" y="0" w="35" h="10" font="FNT_7B" name="time" bg="BLACK"
 *         fg="WHITE"/> <label x="0" y="0" w="200" h="10" font="FNT_7S"
 *         name="nextstop" bg="BLACK" fg="WHITE"/>
 * 
 * <!-- saisie cachee --> <label x="0" y="0" w="1" h="1" font="" name="passwd"
 * bg="BLACK" fg="BLACK" edit="num" max="4" /> <!-- fin saisie cachee -->
 * 
 * <label x="0" y="12" w="240" h="9" font="FNT_9B" name="line1" textid=""/>
 * <label x="0" y="25" w="240" h="9" font="FNT_9S" name="line2" textid=""/>
 * <label x="0" y="34" w="240" h="8" font="FNT_7S" name="msg1" textid=""/>
 * <label x="0" y="44" w="240" h="8" font="FNT_7S" name="msg2" textid=""/>
 * 
 * <!-- V pour valider ou C pour corriger--> <label x="1" y="1" w="1" h="1"
 * font="FNT_8S" name="16" halign="left"/>
 * 
 * <!--Les softKeys F1 a F5--> <label x="0" y="54" w="44" h="10" font="FNT_7S"
 * bg="BLACK" fg="WHITE" name="2"/> <label x="49" y="54" w="44" h="10"
 * font="FNT_7S" bg="BLACK" fg="WHITE" name="7"/> </container>
 */
public class MmiDialogInWrapper4Arret extends MmiDialogInWrapper {
	// Singleton
	private static MmiDialogInWrapper4Arret INSTANCE = null;

	private MmiDialogInWrapper4Arret() {
		super();
	}

	public synchronized static MmiDialogInWrapper4Arret getNewInstance(
			String etat) {
		if (INSTANCE == null)
			INSTANCE = new MmiDialogInWrapper4Arret();
		INSTANCE.reset();
		INSTANCE.setDialogName(MmiConstantes.ARRET);
		INSTANCE.setAppId(etat);
		INSTANCE.resetDialogIn();
		return INSTANCE;
	}

	private String _line1;
	private String _line2;
	private String _nextStop;

	public String abstractGetLabel4Key(String key) {
		if (key.equalsIgnoreCase(F1))
			return "2";
		if (key.equalsIgnoreCase(F2))
			return "7";
		return null;
	}

	public void update(int delai) {
		setDelaiAvantDepart(delai);
	}

	public void setDepartImminent() {
		_line1 = Messages.getString("departImminent");
	}

	public void setDelaiAvantDepart(int delai) {
		if (delai <= 0)
			delai = 0;
		if (delai <= 1)
			setDepartImminent();
		else
			_line1 = Messages.getString("departDans") + Integer.toString(delai)
					+ Messages.getString("min");
	}

	public void setTerminusDepart(String arret) {
		_line2 = Messages.getString("a") + arret;
	}

	public void setNextStop(String arret) {
		_nextStop = arret;
	}

	public void setLine1(String line1) {
		_line1 = line1;
	}

	public void setLine2(String line2) {
		_line2 = Messages.getString("departA") + line2;
	}

	public void abstractGetDialogIn() {
		setLabelValue(LABEL_LINE1, _line1);
		setLabelValue(LABEL_LINE2, _line2);
		setLabelValue(NEXTSTOP, _nextStop);
	}

	protected void reset() {
		_line1 = null;
		_line2 = null;
		_nextStop = null;
	}

}
