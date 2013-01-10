/**
 * 
 */
package org.avm.hmi.mmi.application.actions;

/**
 * @author lbr
 * 
 */
public class States {
	public static final String ETAT_AUTH = "attente saisie authentification";

	public static final String ETAT_CHOIX_ACT = "choix Activite";

	public static final String ETAT_SEG = "attente saisie segment";

	public static final String ETAT_VALID_SEG = "valid segment";

	public static final String ETAT_COURSE_SEG = "en course commerciale";

	public static final String ETAT_COURSE_SEG_DA = "course commerciale prelastStop";

	public static final String ETAT_VALID_FIN_SEG = "valid fin seg";

	public static final String ETAT_VALID_SO = "valid service occasionnel";

	public static final String ETAT_COURSE_SO = "en service occasionnel";

	public static final String ETAT_VALID_FIN_SO = "valid fin service occasionnel";

	public static final String ETAT_VALID_KM = "valid kms a vide";

	public static final String ETAT_COURSE_KM = "en kms a vide";

	public static final String ETAT_VALID_FIN_KM = "valid fin kms a vide";

	public static final String ETAT_VALID_HS = "valid hors service";

	public static final String ETAT_HS = "en panne";

	public static final String ETAT_VALID_FIN_HS = "valid fin hors service";

	public static final String ETAT_DECOMPTE_T = "decompte temps avant depart";

	public static final String ETAT_CALL = "entre ring et raccrocher";

	public static final String ETAT_PROGRESSBAR = "Attente lancement";

	public static final String ETAT_FIN = "Fin application";

	public static final String ALL = "Tous les etats";

	public static final String ETAT_START = "Debut application";

}
