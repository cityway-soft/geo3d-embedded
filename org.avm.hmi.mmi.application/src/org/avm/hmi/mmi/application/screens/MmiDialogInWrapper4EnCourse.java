/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

/**
 * @author lbr Voir bdd/content/win/ENCOURSE.xml <?xml version="1.0"
 *         encoding="UTF-8"?> <container name="ENCOURSE" menu="MAIN"> <clock
 *         x="205" y="0" w="35" h="10" font="FNT_7B" name="time" bg="BLACK"
 *         fg="WHITE"/> <label x="0" y="0" w="200" h="10" font="FNT_7S"
 *         name="nextstop" bg="BLACK" fg="WHITE"/>
 * 
 * <!-- saisie cachee --> <label x="0" y="0" w="1" h="1" font="" name="passwd"
 * bg="BLACK" fg="BLACK" edit="num" max="4"/> <!-- fin saisie cachee -->
 * 
 * <label x="0" y="14" w="240" h="15" font="FNT_9B" name="line1" textid=""/>
 * <label x="0" y="34" w="240" h="8" font="FNT_7S" name="msg1" textid=""/>
 * <label x="0" y="44" w="240" h="8" font="FNT_7S" name="msg2" textid=""/>
 * 
 * <!-- V pour valider ou C pour corriger--> <label x="1" y="1" w="1" h="1"
 * font="FNT_8S" name="16" halign="left"/>
 * 
 * <!--Les softKeys F1 a F5--> <label x="0" y="54" w="44" h="10" font="FNT_7S"
 * bg="BLACK" fg="WHITE" name="2"/> <label x="195" y="54" w="44" h="10"
 * font="FNT_7S" bg="BLACK" fg="WHITE" name="19"/> </container>
 */
public class MmiDialogInWrapper4EnCourse extends MmiDialogInWrapper {
	// Singleton
	private static MmiDialogInWrapper4EnCourse INSTANCE = null;

	private MmiDialogInWrapper4EnCourse() {
		super();
	}

	public synchronized static MmiDialogInWrapper4EnCourse getNewInstance(
			String etat) {
		if (INSTANCE == null)
			INSTANCE = new MmiDialogInWrapper4EnCourse();
		INSTANCE.reset();
		INSTANCE.setDialogName(MmiConstantes.ENCOURSE);
		INSTANCE.setAppId(etat);
		return INSTANCE;
	}

	private String _line1;
	private String _nextStop;

	public String abstractGetLabel4Key(String key) {
		if (key.equalsIgnoreCase(F1))
			return "2";
		if (key.equalsIgnoreCase(F5))
			return "19";
		return null;
	}

	public void setAvanceRetard(int iar) {
		if (iar > 0) {
			_line1 = Messages.getString("Display.retard") + " " + (int) iar
					+ " min";
		} else {
			_line1 = Messages.getString("Display.avance") + " " + -((int) iar)
					+ " min";
		}
	}

	public void setNextStop(String prochainArret) {
		_nextStop = prochainArret;
	}

	public void abstractGetDialogIn() {
		setLabelValue(LABEL_LINE1, _line1);
		setLabelValue(NEXTSTOP, _nextStop);
	}

	protected void reset() {
		_line1 = null;
		_nextStop = null;
	}

}
