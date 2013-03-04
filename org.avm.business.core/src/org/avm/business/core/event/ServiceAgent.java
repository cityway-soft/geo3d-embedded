package org.avm.business.core.event;

import java.io.Serializable;
import java.util.Hashtable;

public class ServiceAgent implements Event, Serializable {

	public static final int SERVICE_OCCASIONNEL = 9999;

	public static final int SERVICE_KM_A_VIDE = 10000;

	private Course[] _courses;

	private Hashtable _hash;

	private int _idu;

	boolean _correct;

	boolean _planifie = false;

	boolean _termine = false;

	private final static String AUTOMATIC = "automatic";
	
	private String automaticLabel = AUTOMATIC;

	public String getAutomaticLabel() {
		return automaticLabel;
	}

	public void setAutomaticLabel(String automaticLabel) {
		this.automaticLabel = automaticLabel;
	}

	// FLA ajout de la notion de libelle
	private String libelle = null;

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public boolean isAutomaticCourse() {
		//return true;
		return this.libelle.equals(automaticLabel);
	}

	public ServiceAgent(boolean correct, int idu, Course[] courses) {
		_correct = correct;
		_courses = courses;
		if (_hash == null && _courses != null) {
			init();
		}
		_idu = idu;
	}

	private void init() {
		_hash = new Hashtable();

		for (int i = 0; i < _courses.length; i++) {
			int crs_idu = _courses[i].getIdu();
			_hash.put(new Integer(crs_idu), _courses[i]);
		}
	}

	public void setPlanifie(boolean b) {
		_planifie = b;
	}

	public boolean isPlanifie() {
		return _planifie;
	}

	public boolean isCorrect() {
		return _correct;
	}

	public int getNbCourse() {
		return (_courses == null) ? 0 : _courses.length;
	}

	public Course getCourseByRang(int i) {
		return _courses[i];
	}

	public Course getNextCourse(int currentCrsIdu) {
		// dans le cas d'une fin de course d'un service spécial
		if (_courses == null)
			return null;
		for (int i = 0; i < _courses.length; i++) {
			if (_courses[i].getIdu() == currentCrsIdu) {
				if ((i + 1) < _courses.length) {
					return _courses[i + 1];
				}
			}
		}
		return null;
	}

	public Course getCourseByIdu(int idu) {
		if (_hash == null && _courses != null) {
			init();
		}
		return (Course) _hash.get(new Integer(idu));
	}

	public int getIdU() {
		return _idu;
	}

	public String toString() {
		return _idu
				+ (_correct ? (" ( " + getNbCourse() + " course(s) )")
						: (" INCORRECT"));
	}

	public Course[] getCourses() {
		return _courses;
	}

	public void setTermine(boolean end) {
		_termine = end;
	}

	public boolean isTermine() {
		return _termine || _courses[_courses.length - 1].isTerminee();
	}

}
