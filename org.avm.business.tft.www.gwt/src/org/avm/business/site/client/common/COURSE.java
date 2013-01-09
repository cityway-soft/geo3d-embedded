package org.avm.business.site.client.common;

public class COURSE {

	private int CRS_ID;

	private int CRS_IDU;

	private String CRS_NOM;

	private int CRS_DEPART;

	private int LGN_IDU;

	private int LGN_AMPLITUDE;

	private int LGN_CHEVAUCHEMENT;

	private int CRD_STATUT;

	private POINT[] POINTS;

	private String LGN_NOM;

	public COURSE() {
		this(0, 0, "", 0, 0, "", 0, 0, 0);
		POINT[] points = new POINT[0];
		setPOINTS(points);
	}

	public COURSE(int crs_id, int crs_idu, String crs_nom, int crs_depart,
			int lgn_idu, String lgn_nom, int lgn_amplitude,
			int lgn_chevauchement, int crd_statut) {
		super();
		CRS_ID = crs_id;
		CRS_IDU = crs_idu;
		CRS_NOM = crs_nom;
		CRS_DEPART = crs_depart;
		LGN_IDU = lgn_idu;
		LGN_NOM = lgn_nom;
		LGN_AMPLITUDE = lgn_amplitude;
		LGN_CHEVAUCHEMENT = lgn_chevauchement;
		CRD_STATUT = crd_statut;
	}

	public int getCRS_ID() {
		return CRS_ID;
	}

	public int getCRS_IDU() {
		return CRS_IDU;
	}

	public String getCRS_NOM() {
		return CRS_NOM;
	}

	public int getCRS_DEPART() {
		return CRS_DEPART;
	}

	public int getLGN_IDU() {
		return LGN_IDU;
	}

	public String getLGN_NOM() {
		return LGN_NOM;
	}

	public int getLGN_AMPLITUDE() {
		return LGN_AMPLITUDE;
	}

	public int getLGN_CHEVAUCHEMENT() {
		return LGN_CHEVAUCHEMENT;
	}

	public int getCRD_STATUT() {
		return CRD_STATUT;
	}

	public POINT[] getPOINTS() {
		return POINTS;
	}

	protected void setCRS_ID(int crs_id) {
		CRS_ID = crs_id;
	}

	protected void setCRS_IDU(int crs_idu) {
		CRS_IDU = crs_idu;
	}

	protected void setCRS_NOM(String crs_nom) {
		CRS_NOM = crs_nom;
	}

	protected void setCRS_DEPART(int crs_depart) {
		CRS_DEPART = crs_depart;
	}

	protected void setLGN_IDU(int lgn_idu) {
		LGN_IDU = lgn_idu;
	}

	protected void setLGN_NOM(String lgn_nom) {
		LGN_NOM = lgn_nom;
	}

	protected void setLGN_AMPLITUDE(int lgn_amplitude) {
		LGN_AMPLITUDE = lgn_amplitude;
	}

	protected void setLGN_CHEVAUCHEMENT(int lgn_chevauchement) {
		LGN_CHEVAUCHEMENT = lgn_chevauchement;
	}

	protected void setCRD_STATUT(int crd_statut) {
		CRD_STATUT = crd_statut;
	}

	protected void setPOINTS(POINT[] points) {
		POINTS = points;
	}

}
