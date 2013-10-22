package org.avm.business.core.event;

import java.util.Hashtable;

public class Course implements Event {
	private static final int DEFAULT_AMPLITUDE = 25;

	private static final int DEFAULT_CHEVAUCHEMENT = 10;

	private int _crs_idu;

	private int _crs_id;

	private Point[] _points;

	private String _lgn_nom;

	private int _lgn_idu;

	private int _depart;

	private String _destination;

	private Hashtable _hashPoints = null;

	private int _lgn_amplitude;

	private int _lgn_chevauchement;

	private boolean _terminee = false;

	private String _crs_nom;

	private String _pcr_nom;

	private int _pcr_idu;
	
	private int iti_sens;

	public Course(int idu, int id, String crs_nom, int depart,
			String destination, String ligneNom, int ligneIdu,
			String parcoursNom, int parcoursIdu, int amplitude,
			int chevauchement, int sens) {
		_crs_idu = idu;
		_crs_id = id;
		_crs_nom = crs_nom;
		_lgn_nom = ligneNom;
		_points = null;
		_depart = depart;
		_lgn_idu = ligneIdu;
		_lgn_amplitude = (amplitude == 0) ? DEFAULT_AMPLITUDE : amplitude;
		_lgn_chevauchement = (chevauchement == 0) ? DEFAULT_CHEVAUCHEMENT
				: chevauchement;
		_pcr_nom = parcoursNom;
		_pcr_idu = parcoursIdu;
		_destination = destination;
		iti_sens = sens;
	}

//	public Course(Course course, Point[] points) {
//		this(course._crs_idu, course._crs_id, course._crs_nom, course._depart,
//				course._destination, course._lgn_nom, course._lgn_idu,
//				course._pcr_nom, course._pcr_idu, course._lgn_amplitude,
//				course._lgn_chevauchement);
//		_points = points;
//		if (points != null && points.length > 1
//				&& points[points.length - 1] != null) {
//			_destination = points[points.length - 1].getNom();
//		}
//		if (points.length > 0) {
//			String val = points[points.length - 1].getAttribute(Point.NOTIFY_POINT);
//			if (val == null || val.toUpperCase().indexOf("E") == -1){
//				val += "E";
//			}
//			points[points.length - 1].setAttribute(Point.NOTIFY_POINT, val);
//		}
//	}
	
	public void setPoints(Point[] points) {
		_points = points;
	}
	
	public void updatePoints(){
		Point[] points = _points;
		if (points != null && points.length > 1
				&& points[points.length - 1] != null) {
			_destination = points[points.length - 1].getNom();
		}
		if (points.length > 0) {
			String val = points[points.length - 1].getAttribute(Point.ATTRIBUT_NOTIFY_POINT);
			if (val == null || val.toUpperCase().indexOf("E") == -1){
				val += "E";
			}
			points[points.length - 1].setAttribute(Point.ATTRIBUT_NOTIFY_POINT, val);
		}
	}

	public String getNom() {
		return _crs_nom;
	}

	public boolean isTerminee() {
		return _terminee || (_points != null && _points.length > 1 && _points[_points.length - 1].getAttente()==0);
	}

	public void setTerminee(boolean b) {
		_terminee = b;
	}

	public int getIdu() {
		return _crs_idu;
	}

	public Point[] getPoints() {
		return _points;
	}

	public Point[] getPointAvecId(int pnt_id) {
		if (_points == null)
			return null;
		if (_hashPoints == null) {
			_hashPoints = new Hashtable();
			Point[] p;
			Integer id;
			for (int i = 0; i < _points.length; i++) {
				id = new Integer(_points[i].getId());
				p = (Point[]) _hashPoints.get(id);
				if (p != null) {
					Point[] pt = new Point[p.length + 1];
					for (int j = 0; j < p.length; j++) {
						pt[j] = p[j];
					}
					pt[p.length] = _points[i];
					p = pt;
				} else {
					p = new Point[1];
					p[0] = _points[i];
				}
				_hashPoints.put(new Integer(_points[i].getId()), p);
			}
		}
		return (Point[]) _hashPoints.get(new Integer(pnt_id));
	}

	public Point getPointAPartirRangAvecId(int rang, int id) {
		Point[] p = getPointAvecId(id);
		if (p == null)
			return null;
		for (int i = 0; i < p.length; i++) {
			if (p[i].getRang() > rang) {
				return p[i];
			}
		}
		return null;
	}

	public Point getPointAvecRang(int i) {
		int idx = i - 1;
		if (idx < getNombrePoint() && idx >= 0) {
			return _points[idx];
		}
		return null;
	}

	/**
	 * Deduit hh:mm a partir du nombre de secondes ecoulees depuis minuit.
	 * 
	 * @param horaire
	 *            en secondes depuis minuit
	 * @return l'heure au format hh:mm
	 */
	private String getHeureDepart(int horaire) {
		return Point.formatHour(horaire);
	}

	public String getHeureDepart() {
		return getHeureDepart(_depart);
	}

	public int getId() {
		return _crs_id;
	}

	public int getNombrePoint() {
		int nb = 0;
		if (_points != null) {
			nb = _points.length;
		}
		return nb;
	}

	public Point getTerminusArrive() {
		Point res = null;
		if (_points != null) {
			res = _points[_points.length - 1];
		}
		return res;
	}

	public Point getTerminusDepart() {
		Point res = null;
		if (_points != null) {
			res = _points[0];
		}
		return res;
	}

	public String getLigneNom() {
		return _lgn_nom;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		if (_terminee) {
			buf.append("EFFECTUEE - ");
		}
		buf.append("(");
		buf.append(getIdu());
		buf.append(") ");
		buf.append(getHeureDepart(_depart));
		buf.append(": ");
		buf.append(getDestination() + " - lig " + getLigneNom() + "("
				+ getLigneIdu() + ")" + " - pcr " + getParcoursNom() + "("
				+ getParcoursIdu() + ")");

		return buf.toString();
	}

	public int getDepart() {
		return _depart;
	}

	public String getDestination() {
		return _destination;
	}

	public int getLigneIdu() {
		return _lgn_idu;
	}

	public int getParcoursIdu() {
		return _pcr_idu;
	}

	public String getParcoursNom() {
		return _pcr_nom;
	}

	public int getAmplitude() {
		return _lgn_amplitude;
	}

	public int getChevauchement() {
		return _lgn_chevauchement;
	}
	
	public int getSens() {
		return iti_sens;
	}
	
	public void setSens(int sens) {
		iti_sens = sens;
	}

	public Object clone() {
		Course clone = new Course(this._crs_idu, this._crs_id, this._crs_nom, this._depart,
				this._destination, this._lgn_nom, this._lgn_idu,
				this._pcr_nom, this._pcr_idu, this._lgn_amplitude,
				this._lgn_chevauchement, this.iti_sens);
		clone.setPoints(this.getPoints());
		return clone;
	}

	public void setAvanceRetard(Point point, int ar) {
		if (point == null)
			return;
		int idx = point.getRang();
		int horaire;
		for (int i = idx; i <= getNombrePoint(); i++) {
			point = getPointAvecRang(i);
			horaire = point.getArriveeTheorique() + ar * 60;
			point.setArriveeTempsReel(horaire);
		}
	}

	public void setPointDesservi(Point p) {
		p.setDesservi(true);
		if (p.getRang() > 1) {
			Point pp = getPointAvecRang(p.getRang() - 1);
			if (pp.isDesservi() == false) {
				for (int i = 1; i < p.getRang(); i++) {
					getPointAvecRang(i).setDesservi(true);
				}
			}
		}
	}

}
