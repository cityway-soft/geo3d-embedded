package org.avm.business.site.client.common;

public interface Constantes {
	public static final int STATE_INITIAL = 0;
	public static final int STATE_ATTENTE_SAISIE_SERVICE = 1;
	public static final int STATE_ATTENTE_SAISIE_COURSE = 2;
	public static final int STATE_ATTENTE_DEPART = 3;
	public static final int STATE_EN_COURSE_HORS_ITINERAIRE = 4;
	public static final int STATE_EN_COURSE_SERVICE_SPECIAL = 5;
	public static final int STATE_EN_PANNE = 6;
	public static final int STATE_EN_COURSE_ARRET_SUR_ITINERAIRE = 7;
	public static final int STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE = 8;
	public static final String MESSAGES = "messages"; //$NON-NLS-1$

	String JEX_DATE = "jex_date"; //$NON-NLS-1$
	String TIME = "time"; //$NON-NLS-1$

	String STATUT = "statut"; //$NON-NLS-1$

	String COURSE = "course"; //$NON-NLS-1$
	String CRS_ID = "crs_id"; //$NON-NLS-1$
	String CRS_IDU = "crs_idu"; //$NON-NLS-1$
	String CRS_NOM = "crs_nom"; //$NON-NLS-1$
	String CRS_DEPART = "crs_depart"; //$NON-NLS-1$
	String LGN_IDU = "lgn_idu"; //$NON-NLS-1$
	String LGN_NOM = "lgn_nom"; //$NON-NLS-1$
	String LGN_AMPLITUDE = "lgn_amplitude"; //$NON-NLS-1$
	String LGN_CHEVAUCHEMENT = "lgn_chevauchement"; //$NON-NLS-1$
	String CRD_STATUT = "crd_statut"; //$NON-NLS-1$

	String POINTS = "points"; //$NON-NLS-1$
	String PNT_ID = "pnt_id"; //$NON-NLS-1$
	String PNT_IDU = "pnt_idu"; //$NON-NLS-1$
	String PNT_NOM = "pnt_nom"; //$NON-NLS-1$
	String GRP_NOM = "grp_nom"; //$NON-NLS-1$
	String PNT_X = "pnt_x"; //$NON-NLS-1$
	String PNT_Y = "pnt_y"; //$NON-NLS-1$
	String PSI_DISTANCE = "psi_distance"; //$NON-NLS-1$
	String PSP_GIR = "psp_gir"; //$NON-NLS-1$
	String HOD_ARRIVEE = "hod_arrivee"; //$NON-NLS-1$
	String HOD_ATTENTE = "hod_attente"; //$NON-NLS-1$
	String HOD_ARRIVEE_THEORIQUE = "hod_arrivee_theorique"; //$NON-NLS-1$
	String HOD_ATTENTE_THEORIQUE = "hod_attente_theorique"; //$NON-NLS-1$
	String HOD_RANG = "hod_rang"; //$NON-NLS-1$
	String HOD_STATUT = "hod_statut"; //$NON-NLS-1$


	public static final String DATA_PATH="data"; //$NON-NLS-1$
	
	
}
