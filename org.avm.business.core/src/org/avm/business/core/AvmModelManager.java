package org.avm.business.core;

import java.io.Serializable;

import org.avm.business.core.event.Authentification;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Planification;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.osgi.util.measurement.State;

public class AvmModelManager implements AvmModel, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AvmModelManager() {
		_state = STATE_INITIAL;
	}

	private int _state;

	private Authentification _authentification = null;

	private ServiceAgent _serviceAgent = null;

	private Course _course = null;

	private Planification _planification;

	// FLA avance retard exprimée désormais en secondes
	private int _avanceRetard;

	private boolean _isHorsItineraire;

	private Point _dernierPoint;

	transient private AvmDatasource _datasource;

	private String _error;

	private boolean _depart;

	private Point _prochainPoint;

	private boolean _inside;

	transient private boolean _isGeorefMode;

	private boolean _isVehiculeFull;

	private transient int lastCourseIdu = -1;

	public int getLastCourseIdu() {
		return lastCourseIdu;
	}

	public void setLastCourseIdu(int lastCourseIdu) {
		this.lastCourseIdu = lastCourseIdu;
	}

	public Point getDernierPoint() {
		return _dernierPoint;
	}

	public void reset() {
		_prochainPoint = null;
		_avanceRetard = 0;
		_isHorsItineraire = false;
		_inside = false;
		_depart = false;
	}

	public void setDernierPoint(Point dernierPoint) {
		_dernierPoint = dernierPoint;
		if (_course != null && _course.getPoints() != null) {
			if (dernierPoint == null) {
				_prochainPoint = _course.getPoints()[0];
			} else {
				_prochainPoint = _course.getPointAvecRang(_dernierPoint
						.getRang() + 1);
			}
		}
	}

	public int getDatasourceVersion() {
		int version = -1;
		if (_datasource != null) {
			version = _datasource.getVersion();
		}
		return version;
	}

	public void setDatasource(AvmDatasource datasource) {
		_datasource = datasource;
	}

	public boolean isHorsItineraire() {
		return _isHorsItineraire;
	}

	public void setHorsItineraire(boolean isHorsItineraire) {
		_isHorsItineraire = isHorsItineraire;
	}

	public Authentification getAuthentification() {
		return _authentification;
	}

	public void setAuthentification(Authentification authentification) {
		_authentification = authentification;
	}

	public ServiceAgent getServiceAgent() {
		return _serviceAgent;
	}

	public void setServiceAgent(ServiceAgent serviceAgent) {
		_serviceAgent = serviceAgent;
	}

	public Course getCourse() {
		return _course;
	}

	public void setCourse(Course course) {
		_course = course;
		setDernierPoint(null);
		_avanceRetard = 0;
		_depart = false;
	}

	public State getState() {
		State state = new State(_state, AVM_STATE_NAME);
		return state;
	}

	public String getStateName() {
		return STATE_NAMES[_state];
	}

	public void setState(int state) {
		_state = state;
	}

	public org.avm.business.core.event.Planification getPlanification() {
		if (_planification == null) {
			_planification = new org.avm.business.core.event.Planification(0,
					0, 0, 0, 0, null, 0);
		}
		return _planification;
	}

	public void setPlanification(
			org.avm.business.core.event.Planification planification) {
		_planification = planification;
	}

	public int getAvanceRetard() {
		return _avanceRetard;
	}

	public void setAvanceRetard(int avanceRetard) {
		_avanceRetard = avanceRetard;
		if (_course != null) {
			_course.setAvanceRetard(_dernierPoint, avanceRetard);
		}
	}

	public String getLastError() {
		return _error;
	}

	public void setError(String erreur) {
		_error = erreur;
	}

	public boolean isDepart() {
		return _depart;
	}

	public void setDepart(boolean depart) {
		_depart = depart;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Etat               : " + getStateName() + " (" + getState()
				+ ")");
		buf.append(System.getProperty("line.separator"));

		buf.append("Source des donnees : "
				+ ((_datasource == null) ? "no datasource" : _datasource
						.getClass().getName()));
		buf.append(System.getProperty("line.separator"));

		buf.append("Version des donnees: "
				+ ((_datasource == null) ? "-" : "" + _datasource.getVersion()));
		buf.append(System.getProperty("line.separator"));

		buf.append("Valid. des donnees : periodes="
				+ ((_datasource == null) ? "no datasource" : _datasource
						.getValiditePeriode()
						+ " - proprietes="
						+ _datasource.getValiditePropriete()));
		buf.append(System.getProperty("line.separator"));

		buf.append(System.getProperty("line.separator"));
		buf.append("Authentification   : " + _authentification);
		buf.append(System.getProperty("line.separator"));
		buf.append("Service            : " + _serviceAgent);
		buf.append(System.getProperty("line.separator"));
		buf.append("Course             : " + _course);
		buf.append(System.getProperty("line.separator"));
		buf.append("Dans la balise ?   : " + _inside);
		buf.append(System.getProperty("line.separator"));
		buf.append("Depart ?           : " + _depart);
		buf.append(System.getProperty("line.separator"));
		buf.append("Hors Itineraire ?  : " + _isHorsItineraire);
		buf.append(System.getProperty("line.separator"));
		buf.append("Mode Georef     ?  : " + _isGeorefMode);
		buf.append(System.getProperty("line.separator"));
		buf.append("Vehicule Complet?  : " + _isVehiculeFull);
		buf.append(System.getProperty("line.separator"));
		buf.append("Code gir. courant  : " + getCodeGirouette());
		buf.append(System.getProperty("line.separator"));
		buf.append("Dernier Arret	   : " + _dernierPoint);
		buf.append(System.getProperty("line.separator"));
		buf.append("Prochain Arret     : " + _prochainPoint);
		buf.append(System.getProperty("line.separator"));
		buf.append("Avance Retard      : "
				+ _avanceRetard
				+ " ("
				+ (_avanceRetard < 0 ? "avance "
						+ Point.formatHour(-_avanceRetard) + ")" : "retard "
						+ Point.formatHour(_avanceRetard) + ")")

		);
		buf.append(System.getProperty("line.separator"));
		buf.append("Planification      : " + _planification);
		buf.append(System.getProperty("line.separator"));
		buf.append("Erreur             : " + _error);
		buf.append(System.getProperty("line.separator"));

		return buf.toString();
	}

	public Point getProchainPoint() {
		return _prochainPoint;
	}

	public boolean isInsidePoint() {
		return _inside;
	}

	public void setInsidePoint(boolean inside) {
		_inside = inside;
	}

	public boolean isGeorefMode() {
		return _isGeorefMode;
	}

	public void setGeorefMode(boolean georefMode) {
		_isGeorefMode = georefMode;
	}

	public int getRang() {
		if (_dernierPoint != null) {
			return _dernierPoint.getRang();
		} else {
			return 0;
		}
	}

	public int getCodeGirouette() {
		int code = 0;
		Point dernier = _dernierPoint;
		if (dernier == null && _course != null) {
			dernier = _course.getPointAvecRang(1);
		}
		if (dernier != null && _course != null && _course.getPoints() != null) {
			Point pts[] = _course.getPoints();
			int c;
			for (int i = dernier.getRang(); i > 0; i--) {
				c = pts[i - 1].getCodeGirouette();
				if (c != 0) {
					code = c;
					break;
				}
			}
		}
		return code;
	}

	public boolean isVehiculeFull() {
		return _isVehiculeFull;
	}

	public void setVehiculeFull(boolean b) {
		_isVehiculeFull = b;
	}

}
