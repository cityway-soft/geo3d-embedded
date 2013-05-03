package org.avm.business.tft.model;


public class POINT {

	private int PNT_IDU;

	private String PNT_NOM;

	private String GRP_NOM;

	private float PNT_X;

	private float PNT_Y;
	
	private int PSI_DISTANCE;
	
	private int PSP_GIR;

	private int HOD_ARRIVEE;

	private int HOD_ATTENTE;

	private int HOD_ARRIVEE_THEORIQUE;

	private int HOD_ATTENTE_THEORIQUE;

	private int HOD_RANG;

	private int HOD_STATUT;
	
	

	public POINT() {
		this( 0, "", "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	public POINT(int pnt_idu, String pnt_nom, String grp_nom,
			float pnt_x, float pnt_y, int psi_distance, int psp_gir,
			int hod_arrivee, int hod_attente, int hod_arrivee_theorique,
			int hod_attente_theorique, int hod_rang, int hod_statut) {
		super();
		PNT_IDU = pnt_idu;
		PNT_NOM = pnt_nom;
		GRP_NOM = grp_nom;
		PNT_X = pnt_x;
		PNT_Y = pnt_y;
		PSI_DISTANCE = psi_distance;
		PSP_GIR = psp_gir;
		HOD_ARRIVEE = hod_arrivee;
		HOD_ATTENTE = hod_attente;
		HOD_ARRIVEE_THEORIQUE = hod_arrivee_theorique;
		HOD_ATTENTE_THEORIQUE = hod_attente_theorique;
		HOD_RANG = hod_rang;
		HOD_STATUT = hod_statut;
	}

	public int getPNT_IDU() {
		return PNT_IDU;
	}

	public String getPNT_NOM() {
		return PNT_NOM;
	}

	public String getGRP_NOM() {
		return GRP_NOM;
	}

	public float getPNT_X() {
		return PNT_X;
	}

	public float getPNT_Y() {
		return PNT_Y;
	}

	public int getPSI_DISTANCE() {
		return PSI_DISTANCE;
	}

	public int getPSP_GIR() {
		return PSP_GIR;
	}

	public int getHOD_ARRIVEE() {
		return HOD_ARRIVEE;
	}

	public int getHOD_ATTENTE() {
		return HOD_ATTENTE;
	}

	public int getHOD_ARRIVEE_THEORIQUE() {
		return HOD_ARRIVEE_THEORIQUE;
	}

	public int getHOD_ATTENTE_THEORIQUE() {
		return HOD_ATTENTE_THEORIQUE;
	}

	public int getHOD_RANG() {
		return HOD_RANG;
	}

	public int getHOD_STATUT() {
		return HOD_STATUT;
	}

	protected void setPNT_IDU(int pnt_idu) {
		PNT_IDU = pnt_idu;
	}

	protected void setPNT_NOM(String pnt_nom) {
		PNT_NOM = pnt_nom;
	}

	protected void setGRP_NOM(String grp_nom) {
		GRP_NOM = grp_nom;
	}

	protected void setPNT_X(float pnt_x) {
		PNT_X = pnt_x;
	}

	protected void setPNT_Y(float pnt_y) {
		PNT_Y = pnt_y;
	}

	protected void setPSI_DISTANCE(int psi_distance) {
		PSI_DISTANCE = psi_distance;
	}

	protected void setPSP_GIR(int psp_gir) {
		PSP_GIR = psp_gir;
	}

	protected void setHOD_ARRIVEE(int hod_arrivee) {
		HOD_ARRIVEE = hod_arrivee;
	}

	protected void setHOD_ATTENTE(int hod_attente) {
		HOD_ATTENTE = hod_attente;
	}

	protected void setHOD_ARRIVEE_THEORIQUE(int hod_arrivee_theorique) {
		HOD_ARRIVEE_THEORIQUE = hod_arrivee_theorique;
	}

	protected void setHOD_ATTENTE_THEORIQUE(int hod_attente_theorique) {
		HOD_ATTENTE_THEORIQUE = hod_attente_theorique;
	}

	protected void setHOD_RANG(int hod_rang) {
		HOD_RANG = hod_rang;
	}

	protected void setHOD_STATUT(int hod_statut) {
		HOD_STATUT = hod_statut;
	}
	
}
