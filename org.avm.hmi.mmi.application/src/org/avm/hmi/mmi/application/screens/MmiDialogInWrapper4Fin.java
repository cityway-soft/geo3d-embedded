/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

/**
 * @author lbr Voir bdd/content/win/FIN.xml <container name="FIN" menu="MAIN">
 *         <clock x="204" y="0" w="36" h="10" font="FNT_7B" name="time"
 *         bg="BLACK" fg="WHITE"/>
 * 
 * <label x="0" y="14" w="240" h="18" font="FNT_9B" name="text"
 * textid="INF_FIN"/> <label x="0" y="34" w="240" h="8" font="FNT_7S"
 * name="msg1" textid=""/> <label x="0" y="44" w="240" h="8" font="FNT_7S"
 * name="msg2" textid=""/>
 * 
 * <!-- V pour valider ou C pour corriger-->
 * 
 * <!--Les softKeys F1 a F5--> </container>
 */
public class MmiDialogInWrapper4Fin extends MmiDialogInWrapper {
	// Singleton
	private static MmiDialogInWrapper4Fin INSTANCE = null;

	private MmiDialogInWrapper4Fin() {
		super();
	}

	public synchronized static MmiDialogInWrapper4Fin getNewInstance(String etat) {
		if (INSTANCE == null)
			INSTANCE = new MmiDialogInWrapper4Fin();
		INSTANCE.reset();
		INSTANCE.setDialogName(MmiConstantes.FIN);
		INSTANCE.setAppId(etat);
		return INSTANCE;
	}

	public String abstractGetLabel4Key(String key) {
		return null;
	}

	public void abstractGetDialogIn() {
	}

	protected void reset() {
	}
}
