/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

/**
 * @author lbr **Voir bdd/content/win/INFO_APPEL.xml <?xml version="1.0"
 *         encoding="UTF-8"?> <container name="INFO_APPEL" menu="MAIN"> <clock
 *         x="204" y="0" w="36" h="10" font="FNT_7B" name="time" bg="BLACK"
 *         fg="WHITE"/>
 * 
 * <label x="0" y="14" w="240" h="15" font="FNT_9B" name="line1" textid=""/>
 * <label x="0" y="34" w="240" h="8" font="FNT_7S" name="msg1" textid=""/>
 * <label x="0" y="44" w="240" h="8" font="FNT_7S" name="msg2" textid=""/>
 * 
 * <!-- V pour valider ou C pour corriger--> <label x="1" y="1" w="1" h="1"
 * font="FNT_8S" name="16" halign="left"/>
 * 
 * <!--Les softKeys F1 a F5--> <label x="0" y="54" w="44" h="10" font="FNT_7S"
 * bg="BLACK" fg="WHITE" name="4"/> </container>
 */
public class MmiDialogInWrapper4InfoAppel extends MmiDialogInWrapper {
	// Singleton
	private static MmiDialogInWrapper4InfoAppel INSTANCE = null;
	private String _line1;

	private MmiDialogInWrapper4InfoAppel() {
		super();
	}

	public synchronized static MmiDialogInWrapper4InfoAppel getNewInstance(
			String etat) {
		if (INSTANCE == null)
			INSTANCE = new MmiDialogInWrapper4InfoAppel();
		INSTANCE.reset();
		INSTANCE.setDialogName(MmiConstantes.INFO_APPEL);
		INSTANCE.setAppId(etat);
		return INSTANCE;
	}

	public String abstractGetLabel4Key(String key) {
		if (key.equalsIgnoreCase(F1))
			return "4";
		return null;
	}

	public void setCallingInfos(String callingNum) {
		_line1 = callingNum;
	}

	public void abstractGetDialogIn() {
		setLabelValue(LABEL_LINE1, _line1);
	}

	protected void reset() {
		_line1 = null;
	}

}
