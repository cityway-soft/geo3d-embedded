/**
 * 
 */
package org.avm.hmi.mmi.application.screens;

/**
 * @author lbr
 * 
 */
public interface MmiConstantes {
	// Nom des dialog (voir mmi/content/win/*.xml; attribut name de l'élément
	// container)
	public static final String SAISIE = "SAISIE"; // Ecran de saisie de donnée
													// (Matricule, segment)
	public static final String ARRET = "ARRET"; // Ecran Arret
	public static final String PROGRESSBAR = "ATTENTE"; // Ecran barre de
														// progression
	public static final String CHOIX_ACT = "CHOIX_ACT"; // Ecran choix de
														// l'activité
	public static final String ENCOURSE = "ENCOURSE"; // Ecran en course
	public static final String FIN = "FIN"; // Ecran "Fin de l'application"
	public static final String INFO = "INFO"; // Ecran avec une ligne de
												// message libre
	public static final String INFO_APPEL = "INFO_APPEL"; // Ecran appel
															// entrant
	public static final String INFO_KM = "INFO_KM"; // Ecran service Kms à vide
	public static final String INFO_SO = "INFO_SO"; // Ecran service occasionnel
	public static final String VALID_CHOIX = "VALID_CHOIX"; // Ecran validation
															// d'un choix
	public static final String VALID_CHOIX2 = "VALID_CHOIX2"; // Ecran
																// validation
																// d'un choix
																// avec deux
																// lignes de
																// texte

	// public static final String ADARRET="AVANTDERNIERARRET";
	// public static final String ADARRET_F5="AVANTDERNIERARRET_F5";

	public static final String F1 = "F1";
	public static final String F2 = "F2";
	public static final String F3 = "F3";
	public static final String F4 = "F4";
	public static final String F5 = "F5";
	public static final String SK_APP_EN_COURS = "SK_APP_EN_COURS";
	public static final String SK_RACCROCHER = "SK_RACCROCHER";
	public static final String SK_DECROCHER = "SK_DECROCHER";
	public static final String SK_IN = "SK_IN";
	public static final String SK_OUT = "SK_OUT";
	public static final String SK_DEVIATION = "SK_DEVIATION";
	public static final String SK_DEVIE = "SK_DEVIE";
	// public static final String SK_VALID_MSG = "SK_VALID";

	public static final String INF_SERV_OCC = "INF_SERV_OCC";
	public static final String INF_KMS_AV = "INF_KMS_AV";
	public static final String INF_FIN_HS = "INF_FIN_HS";
	public static final String INF_FIN_SO = "INF_FIN_SO";
	public static final String INF_FIN_KM = "INF_FIN_KA";
	public static final String INF_HS = "INF_HS";
	public static final String INF_CALLING_CM = "INF_CALLING_CM";
	public static final String INF_RINGING = "INF_RINGING";

	public static final String LABEL_LINE1 = "line1";
	public static final String LABEL_LINE2 = "line2";
	public static final String LABEL_SAISIE = "saisie";
	public static final String LABEL_MSG1 = "msg1";
	public static final String LABEL_MSG2 = "msg2";
	public static final int SIZE_MAX_MSG = 58;
	public static final String NEXTSTOP = "nextstop";
	public static final String INSTR_CONF = "INSTR_CONF";
	public static final String SK_FIN_PANNE = "SK_FIN_HS";
}
