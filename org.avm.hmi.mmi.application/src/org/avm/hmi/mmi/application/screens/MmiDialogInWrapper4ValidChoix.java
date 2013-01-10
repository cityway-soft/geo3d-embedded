/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

/**
 * @author lbr **Voir bdd/content/win/VALID_CHOIX.xml <?xml version="1.0"
 *         encoding="UTF-8"?> <container name="VALID_CHOIX" menu="MAIN"> <clock
 *         x="204" y="0" w="36" h="10" font="FNT_7B" name="time" bg="BLACK"
 *         fg="WHITE"/> <!-- saisie cachee --> <label x="0" y="0" w="1" h="1"
 *         font="" name="passwd" bg="BLACK" fg="BLACK" edit="num" max="4"/> <!--
 *         fin saisie cachee --> <label x="0" y="14" w="240" h="18"
 *         font="FNT_9B" name="line1" textid=""/>
 * 
 * <!-- V pour valider ou C pour corriger--> <label x="1" y="1" w="1" h="1"
 * font="FNT_8S" name="16" halign="left"/> <label x="1" y="34" w="119" h="16"
 * font="FNT_8S" name="1" halign="left"/> <label x="121" y="34" w="119" h="16"
 * font="FNT_8S" name="15" halign="left"/>
 * 
 * <!--Les softKeys F1 a F5--> <label x="0" y="54" w="44" h="10" font="FNT_7S"
 * bg="BLACK" fg="WHITE" name="2"/> </container>
 */
public class MmiDialogInWrapper4ValidChoix extends MmiDialogInWrapper {
	// Singleton
	private static MmiDialogInWrapper4ValidChoix INSTANCE = null;

	private MmiDialogInWrapper4ValidChoix() {
		super();
	}

	public synchronized static MmiDialogInWrapper4ValidChoix getNewInstance(
			String etat) {
		if (INSTANCE == null)
			INSTANCE = new MmiDialogInWrapper4ValidChoix();
		INSTANCE.reset();
		INSTANCE.setDialogName(MmiConstantes.VALID_CHOIX);
		INSTANCE.setAppId(etat);
		return INSTANCE;
	}

	private String _line1, _labelId;

	public String abstractGetLabel4Key(String key) {
		if (key.equalsIgnoreCase(F1))
			return "2";
		return null;
	}

	public void setLine1(String line1) {
		_line1 = line1;
	}

	public void setLabelId(String labelId) {
		_labelId = labelId;
	}

	public void abstractGetDialogIn() {
		setLabelValue(MmiDialogInWrapper.LABEL_LINE1, _line1);
		super.setLabelId(MmiDialogInWrapper.LABEL_LINE1, _labelId);
	}

	protected void reset() {
		_line1 = null;
		_labelId = null;
	}

}
