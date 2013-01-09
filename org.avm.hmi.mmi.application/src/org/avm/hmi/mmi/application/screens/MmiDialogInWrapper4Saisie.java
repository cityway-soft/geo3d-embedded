/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

/**
 * @author lbr **Voir bdd/content/win/SAISIE.xml <?xml version="1.0"
 *         encoding="UTF-8"?> <container name="SAISIE" menu="MAIN"> <clock
 *         x="204" y="0" w="36" h="10" font="FNT_7B" name="time" bg="BLACK"
 *         fg="WHITE"/> <label x="0" y="0" w="180" h="10" font="FNT_7S"
 *         name="line1" textid=""/>
 * 
 * <label x="0" y="14" w="117" h="18" font="FNT_9B" name="saisie" textid=""/>
 * <label x="123" y="14" w="117" h="18" font="FNT_9B" name="valeur" bg="BLACK"
 * fg="WHITE" edit="num" max="5"/>
 * 
 * <!-- V pour valider ou C pour corriger--> <label x="1" y="1" w="1" h="1"
 * font="FNT_8S" name="16" halign="left"/> <label x="1" y="34" w="119" h="16"
 * font="FNT_8S" name="1" halign="left"/> <label x="121" y="34" w="119" h="16"
 * font="FNT_8S" name="15" halign="left"/>
 * 
 * <!--Les softKeys F1 a F5--> <label x="0" y="54" w="44" h="10" font="FNT_7S"
 * bg="BLACK" fg="WHITE" name="2"/> </container>
 */
public class MmiDialogInWrapper4Saisie extends MmiDialogInWrapper {
	// Singleton
	private static MmiDialogInWrapper4Saisie INSTANCE = null;

	private MmiDialogInWrapper4Saisie() {
		super();
	}

	public synchronized static MmiDialogInWrapper4Saisie getNewInstance(
			String etat) {
		if (INSTANCE == null)
			INSTANCE = new MmiDialogInWrapper4Saisie();
		INSTANCE.reset();
		INSTANCE.setDialogName(MmiConstantes.SAISIE);
		INSTANCE.setAppId(etat);
		return INSTANCE;
	}

	public static final String MATRICULE = Messages.getString("Matricule");
	public static final String SEGMENT = Messages.getString("Segment");
	private String _prompt;

	public void setPrompt(String label) {
		_prompt = label;
	}

	public String abstractGetLabel4Key(String key) {
		if (key.equalsIgnoreCase(F1))
			return "2";
		return null;
	}

	public void abstractGetDialogIn() {
		setLabelValue(LABEL_SAISIE, _prompt);
	}

	protected void reset() {
		_prompt = null;
	}

}
