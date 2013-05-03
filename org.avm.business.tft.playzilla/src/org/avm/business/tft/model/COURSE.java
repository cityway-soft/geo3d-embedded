package org.avm.business.tft.model;

public class COURSE {

	private int CRS_ID;

	private int CRS_IDU;

	private String CRS_NOM;

	private int CRS_DEPART;

	private String LGN_IDU;
	
	private String LGN_NOM;
	
	private String LGN_CODE;


	private int ITI_IDU;
	
	private POINT[] POINTS;
	
	
	
	public COURSE() {
		this(0,0,"",0,
				0,"", "",0);
		POINT[] points = new POINT[0];
		setPOINTS(points );
	}

	public COURSE(int crs_id, int crs_idu, String crs_nom, int crs_depart,
			int lgn_idu, String lgn_nom, String lgn_code, int iti_idu) {
		super();
		CRS_ID = crs_id;
		CRS_IDU = crs_idu;
		CRS_NOM = crs_nom;
		CRS_DEPART = crs_depart;
		LGN_IDU = Integer.toString(lgn_idu);
		LGN_NOM = lgn_nom;
		LGN_CODE=lgn_code;
		ITI_IDU = iti_idu;

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

	public String getLGN_IDU() {
		return LGN_IDU;
	}
	
	public String getLGN_NOM() {
		return LGN_NOM;
	}
	
	public String getLGN_CODE() {
		return LGN_CODE;
	}
	
	protected void setLGN_CODe(String lgn_code) {
		LGN_CODE = lgn_code;
	}

	public int getITI_IDU() {
		return ITI_IDU;
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

	protected void setLGN_IDU(String lgn_idu) {
		LGN_IDU = lgn_idu;
	}
	
	protected void setLGN_NOM(String lgn_nom) {
		LGN_NOM = lgn_nom;
	}
	


	protected void setITI_IDU(int iti_idu) {
		ITI_IDU = iti_idu;
	}

	protected void setPOINTS(POINT[] points) {
		POINTS = points;
	}
	
	
}
