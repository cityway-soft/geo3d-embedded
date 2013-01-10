/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

/**
 * @author lbr
 * 
 * Voir bdd/content/win/CHOIX_ACT.xml <?xml version="1.0" encoding="UTF-8"?>
 * <container name="CHOIX_ACT" menu="MAIN"> <clock x="204" w="36" h="10"
 * font="FNT_7B" name="time" bg="BLACK" fg="WHITE"/>
 * 
 * <label y="10" h="10" font="FNT_7S" name="line1" textid=""/> <label y="24"
 * h="10" font="FNT_9B" name="CHOIX" textid="INSTR_CHOIX"/>
 * 
 * <label x="0" y="34" w="240" h="8" font="FNT_7S" name="msg1" textid=""/>
 * <label x="0" y="44" w="240" h="8" font="FNT_7S" name="msg2" textid=""/>
 * 
 * <!-- V pour valider ou C pour corriger--> <label x="1" y="1" w="1" h="1"
 * font="FNT_8S" name="16" halign="left"/>
 * 
 * <!--Les softKeys F1 a F5--> <label x="0" y="54" w="44" h="10" font="FNT_7S"
 * bg="BLACK" fg="WHITE" name="2"/> <label x="49" y="54" w="44" h="10"
 * font="FNT_7S" bg="BLACK" fg="WHITE" name="6"/> <label x="98" y="54" w="44"
 * h="10" font="FNT_7S" bg="BLACK" fg="WHITE" name="10"/> <label x="147" y="54"
 * w="44" h="10" font="FNT_7S" bg="BLACK" fg="WHITE" name="11"/> <label x="196"
 * y="54" w="44" h="10" font="FNT_7S" bg="BLACK" fg="WHITE" name="12"/>
 * </container>
 */
public class MmiDialogInWrapper4ChoixAct extends MmiDialogInWrapper {
	// Singleton
	private static MmiDialogInWrapper4ChoixAct INSTANCE = null;

	private MmiDialogInWrapper4ChoixAct() {
		super();
	}

	public synchronized static MmiDialogInWrapper4ChoixAct getNewInstance(
			String etat) {
		if (INSTANCE == null)
			INSTANCE = new MmiDialogInWrapper4ChoixAct();
		INSTANCE.reset();
		INSTANCE.setDialogName(MmiConstantes.CHOIX_ACT);
		INSTANCE.setAppId(etat);
		return INSTANCE;
	}

	private String _line1;

	public String abstractGetLabel4Key(String key) {
		if (key.equalsIgnoreCase(F1))
			return "2";
		if (key.equalsIgnoreCase(F2))
			return "6";
		if (key.equalsIgnoreCase(F3))
			return "10";
		if (key.equalsIgnoreCase(F4))
			return "11";
		if (key.equalsIgnoreCase(F5))
			return "12";
		return null;
	}

	public void setLine(String message) {
		if (message == null)
			clearMessages();
		_line1 = message;
	}

	public void abstractGetDialogIn() {
		setLabelValue(LABEL_LINE1, _line1);
	}

	protected void reset() {
		_line1 = null;
		clearSoftKeys();
	}

}
