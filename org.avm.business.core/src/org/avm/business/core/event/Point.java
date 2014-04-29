package org.avm.business.core.event;

import java.util.HashMap;

public class Point implements Event {

	public final static int ITL_NONE = 1;

	public final static int ITL_NO_DOWN = 3;

	public final static int ITL_NO_UP = 2;

	private int _idu;

	private int _id;

	private String _nom;

	private int _type;

	private int _arriveeTheorique;

	private int _arriveeTempsReel;

	private int _attente;

	private int _rang;

	private float _distance;

	private float _longitude;

	private float _latitude;

	// private boolean _isEntryNotify = false;

	public static final Integer ATTRIBUT_NOTIFY_POINT = new Integer(40);

	private String _nomReduitGroupePoint;

	private int _codeGirouette = -1;

	private boolean _isDesservi = false;

	private HashMap _attributes = null;

	private int _attenteTheorique;

	private int itl = ITL_NONE;

	public Point(int id, int idu, String nom, int type, float x, float y) {
		_id = id;
		_idu = idu;
		_nom = nom;
		_type = type;
		_longitude = x;
		_latitude = y;
		_attente = -1;
		_nomReduitGroupePoint = "-";
		_attributes = new HashMap();
	}

	public Point(int id, int idu, String nom, int type, int arrivee,
			int attenteTheorique, int rang, float distance, float x, float y,
			int codeGirouette, int itl) {
		this(id, idu, nom, type, x, y);
		_arriveeTheorique = arrivee;
		_arriveeTempsReel = arrivee;
		_attenteTheorique = attenteTheorique;
		_rang = rang;
		_distance = distance;
		if (itl == 0) {
			itl = ITL_NONE;
		}
		this.itl = itl;
		setCodeGirouette(codeGirouette);
	}

	public String getNomReduitGroupePoint() {
		return _nomReduitGroupePoint;
	}

	public int getArriveeTempsReel() {
		return _arriveeTempsReel;
	}

	public void setArriveeTempsReel(int arriveeTempsReel) {
		_arriveeTempsReel = arriveeTempsReel;
	}

	public boolean isDesservi() {
		return _isDesservi;
	}

	public void setDesservi(boolean t) {
		_isDesservi = t;
	}

	public boolean isEntryNotify() {
		boolean result = false;
		String val = getAttribute(ATTRIBUT_NOTIFY_POINT);
		result = (val != null && val.toUpperCase().indexOf("E") != -1);
		return result;
	}

	// public void setEntryNotify(boolean t) {
	// _isEntryNotify = t;
	// }

	public int getArriveeTheorique() {
		return _arriveeTheorique;
	}

	public int getAttente() {
		return _attente;
	}

	public int getAttenteTheorique() {
		return _attenteTheorique;
	}

	public float getDistance() {
		return _distance;
	}

	public int getIdu() {
		return _idu;
	}

	public String getHeureArriveeTempsReel() {
		return formatHour(_arriveeTempsReel);
	}

	public String getHeureArriveeTheorique() {
		return formatHour(_arriveeTheorique);
	}

	public String getHeureDepartTheorique() {
		int horaire = _arriveeTheorique + _attenteTheorique;
		return formatHour(horaire);
	}

	/**
	 * Deduit hh::mm a partir du nombre de secondes ecoulees depuis minuit.
	 * Static car elle peut être appelée par ailleurs (course, par exemple)
	 * 
	 * @param horaire
	 *            en secondes depuis minuit
	 * @return l'heure au format hh:mm
	 */
	static public String formatHour(int timeInSecondsSinceMidnight) {
		double fh = ((double) timeInSecondsSinceMidnight) / 3600.0;
		int h = (int) fh;
		int m = (int) ((timeInSecondsSinceMidnight - (3600 * h)) / 60);
		return (((h > 9) ? "" : "0") + h + "h" + ((m > 9) ? "" : "0") + m);
	}

	public String getNom() {
		return _nom;
	}

	public int getRang() {
		return _rang;
	}

	public int getType() {
		return _type;
	}

	public float getLongitude() {
		return _longitude;
	}

	public float getLatitude() {
		return _latitude;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		String state = null;
		if (getAttente() == 0) {
			state = "=";
		} else if (getAttente() > 0) {
			state = "X";
		} else {
			state = ".";
		}
		buf.append(isDesservi() ? (state) : " ");
		buf.append("] ");
		buf.append("#");
		buf.append(((_rang <= 9) ? "0" : ""));
		buf.append(_rang);
		buf.append(" dep(");
		buf.append(getHeureDepartTheorique());
		buf.append(")");
		buf.append(" arr(");
		buf.append(getHeureArriveeTempsReel());
		buf.append(")");
		buf.append(" att(");
		buf.append(getAttente() < 0 ? 0 : getAttente());
		buf.append("s)");
		buf.append(" idu(");
		buf.append(_idu);
		buf.append(") ");
		buf.append(" id(");
		buf.append(_id);
		buf.append(") ");
		buf.append(" nom(");
		buf.append(_nom);
		buf.append(") ");
		buf.append(" reduit(");
		buf.append(_nomReduitGroupePoint);
		buf.append(") ");
		buf.append(" flagEnt(");
		buf.append(isEntryNotify());
		buf.append(") ");
		buf.append(" ITL(");
		buf.append(getItlLabel());
		buf.append(") ");
		if (_codeGirouette != 0) {
			buf.append(" GIR(");
			buf.append(_codeGirouette);
			buf.append(") ");
		}
		return buf.toString();
	}
	
	private String getItlLabel(){
		switch (itl) {
		case ITL_NO_DOWN:
			return "D";		
		case ITL_NO_UP:
				return "M";


		default:
			return "-";
		}
	}

	public void setNomReduitGroupePoint(String reduit) {
		_nomReduitGroupePoint = reduit;
	}

	public int getId() {
		return _id;
	}

	public int getCodeGirouette() {
		return _codeGirouette;
	}

	public void setArriveeTheorique(int arriveeTheorique) {
		_arriveeTheorique = arriveeTheorique;
	}

	public void setAttenteTheorique(int attente) {
		_attenteTheorique = (attente < 0) ? 0 : attente;
	}

	public void setAttente(int attente) {
		// DLA attention : le champ attente dans le message phoebus Horaire est
		// sur 15 bits et -1 sur 15 bits convertis en int = 32672
		_attente = (attente < 0) ? 0 : attente;
	}

	public void setRang(int rang) {
		_rang = rang;
	}

	public void setDistance(float distance) {
		_distance = distance;
	}

	public void setCodeGirouette(int codeGirouette) {
		_codeGirouette = (codeGirouette > 0) ? codeGirouette : 0;
	}

	public Object clone() {
		Point p = new Point(this._id, this._idu, this._nom, this._type,
				this._arriveeTheorique, this._attente, this._rang,
				this._distance, this._longitude, this._latitude,
				this._codeGirouette, this.itl);
		p._arriveeTheorique = this._arriveeTheorique;
		p._arriveeTempsReel = this._arriveeTempsReel;
		p._nomReduitGroupePoint = this._nomReduitGroupePoint;

		return p;
	}

	public boolean isGeoref() {
		return !((getLongitude() == 0 && getLatitude() == 0) || (getLongitude() == -1 && getLatitude() == -1));
	}

	public void setAttribute(Integer attId, String att_val) {
		if (_attributes != null && attId != null && att_val != null) {
			_attributes.put(attId, att_val);
		}
	}

	public String getAttribute(Integer id) {
		return (String) _attributes.get(id);
	}

	public int getItl() {
		return itl;
	}

	public void setItl(int itl) {
		if (itl == 0){
			itl = ITL_NONE;
		}
		this.itl = itl;
	}
}
