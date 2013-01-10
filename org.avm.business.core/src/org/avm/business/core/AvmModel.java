package org.avm.business.core;

import org.avm.business.core.event.Authentification;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Planification;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.osgi.util.measurement.State;

public interface AvmModel {

	public static final int STATE_INITIAL = 0;
	public static final int STATE_ATTENTE_SAISIE_SERVICE = 1;
	public static final int STATE_ATTENTE_SAISIE_COURSE = 2;
	public static final int STATE_ATTENTE_DEPART = 3;
	public static final int STATE_EN_COURSE_HORS_ITINERAIRE = 4;
	public static final int STATE_EN_COURSE_SERVICE_SPECIAL = 5;
	public static final int STATE_EN_PANNE = 6;
	public static final int STATE_EN_COURSE_ARRET_SUR_ITINERAIRE = 7;
	public static final int STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE = 8;

	public static final String STATE_NAMES[] = { 
			"ETAT_INITIAL",
			"ATTENTE_SAISIE_SERVICE", 
			"ATTENTE_SAISIE_COURSE",
			"ATTENTE_DEPART", 
			"EN_COURSE_HORS_ITINERAIRE", 
			"EN_COURSE_SERVICE_SPECIAL",
			"EN_PANNE", 
			"EN_COURSE_ARRET_SUR_ITINERAIRE",
			"EN_COURSE_INTERARRET_SUR_ITINERAIRE" };
	
	public static final String AVM_STATE_NAME=Avm.class.getName();

//  --- Le code ci-dessous fait planter "lourdement" la JVM au bout d'un certain temps...
//  ---
//	public static final State[] STATES = {
//			new State(STATE_INITIAL, AVM_STATE_NAME),
//			new State(STATE_ATTENTE_SAISIE_SERVICE,
//					AVM_STATE_NAME),
//			new State(STATE_ATTENTE_SAISIE_COURSE,
//					AVM_STATE_NAME),
//			new State(STATE_ATTENTE_DEPART, AVM_STATE_NAME),
//			new State(STATE_EN_COURSE_HORS_ITINERAIRE,
//					AVM_STATE_NAME),
//			new State(STATE_EN_COURSE_SERVICE_SPECIAL,
//					AVM_STATE_NAME),
//			new State(STATE_EN_PANNE, AVM_STATE_NAME),
//			new State(STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
//					AVM_STATE_NAME),
//			new State(STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE,
//					AVM_STATE_NAME)
//
//	};

	public Planification getPlanification();

	public ServiceAgent getServiceAgent();

	public Course getCourse();

	public Point getDernierPoint();

	public Point getProchainPoint();

	public int getCodeGirouette();

	public int getAvanceRetard();

	public boolean isHorsItineraire();

	public boolean isDepart();

	//
	public State getState();

	public Authentification getAuthentification();

	public int getDatasourceVersion();

	public String getLastError();

	public boolean isInsidePoint();

	public boolean isGeorefMode();
	
	public boolean isVehiculeFull();

	public int getRang();

}
